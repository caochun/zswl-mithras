package cn.zswltech.mithras.service.service.projlifecycle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.projlifecycle.ProjStageTotalRSP;
import cn.zswltech.mithras.dto.projlifecycle.ProjectLifecycleListREQ;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projlifecycle.mapper.model.ProjLifecycleListDO;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projlifecycle.mapper.ProjLifecycleEventMapper;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractAocPriceService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractFactoringPriceService;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.contract.enums.contract.ContractStatus.*;
import static cn.zswltech.mithras.contract.enums.contract.ProjItemStatus.CLOSED;

/**
 * @author yibin
 */
@Service
public class ProjLifecycleService {

    public Page<ProjLifecycleListDO> list(ProjectLifecycleListREQ req) {
        //权限控制
        List<Long> deptIds = getBean(SysUserService.class).canViewDeptIds();
        if (deptIds != null && deptIds.isEmpty()) {
            //没有可看的全部部门，说明是项目经理，所以从主办走字段sponsorId
            req.setSponsorId(AccountUtil.getLoginInfo().getId());
        }
        req.setDeptIdList(deptIds);
        Page<ProjLifecycleListDO> pageData = getBean(ProjLifecycleEventMapper.class)
                .mainList(new Page<>(req.getPage(), req.getPageSize()), req);
        List<ProjLifecycleListDO> records = pageData.getRecords();
        fillContractAmount(records);
        fillPaymentAmount(records);
        fillRemainingPrincipal(records);
        pageData.setRecords(records);
        return pageData;
    }

    private void fillContractAmount(List<ProjLifecycleListDO> rspList) {
        if (CollUtil.isEmpty(rspList)) {
            return;
        }
        List<ProjReviewBaseInfo> projReviewList = getBean(ProjReviewBaseInfoService.class)
                .listByIds(rspList.stream().map(ProjLifecycleListDO::getReviewId).collect(Collectors.toSet()));
        if (!projReviewList.isEmpty()) {
            List<ContractBaseInfo> contractList = getBean(ContractBaseInfoService.class).listByProjReviewIds(projReviewList.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
            //已申请
            Map<Long, AtomicLong> groupValueMap = groupProjContractAmount(contractList);
            rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
                AtomicLong v = groupValueMap.get(e.getReviewId());
                if (null != v) {
                    e.setContractAmountApplied(v.get());
                }
            });
            //已生效
            contractList = contractList.stream().filter(e -> !StrUtil.equals(e.getContractStatus(), NEW.name())).collect(Collectors.toList());
            Map<Long, AtomicLong> effectedGroupValueMap = groupProjContractAmount(contractList);
            rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
                AtomicLong v = effectedGroupValueMap.get(e.getReviewId());
                if (null != v) {
                    e.setContractAmountEffected(v.get());
                }
            });
        }
    }

    private Map<Long/*立项id*/, AtomicLong> groupProjContractAmount(List<ContractBaseInfo> contractList) {
        List<ContractLeasePrice> leasePriceList = getBean(ContractLeasePriceService.class).listByContractIds(contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        List<ContractAocPrice> aocPriceList = getBean(ContractAocPriceService.class).listByContractIds(contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        List<ContractFactoringPrice> factoringPriceList = getBean(ContractFactoringPriceService.class).listByContractIds(contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        //
        Map<Long/*合同id*/, AtomicLong/*加总值*/> contractTotalMap = new HashMap<>();
        leasePriceList.forEach(e -> {
            contractTotalMap.putIfAbsent(e.getContractId(), new AtomicLong(0L));
            contractTotalMap.get(e.getContractId()).addAndGet(e.getApplyCreditAmount());
        });
        aocPriceList.forEach(e -> {
            contractTotalMap.putIfAbsent(e.getContractId(), new AtomicLong(0L));
            contractTotalMap.get(e.getContractId()).addAndGet(e.getContractAmount());
        });
        factoringPriceList.forEach(e -> {
            contractTotalMap.putIfAbsent(e.getContractId(), new AtomicLong(0L));
            contractTotalMap.get(e.getContractId()).addAndGet(e.getContractAmount());
        });
        //
        Map<Long/*评审id*/, AtomicLong/*加总值*/> projReviewTotalMap = new HashMap<>();
        contractList.forEach(e -> {
            projReviewTotalMap.putIfAbsent(e.getProjReviewId(), new AtomicLong(0L));
            if (contractTotalMap.containsKey(e.getId())) {
                projReviewTotalMap.get(e.getProjReviewId()).addAndGet(contractTotalMap.get(e.getId()).get());
            }
        });
        return projReviewTotalMap;
    }

    private void fillPaymentAmount(List<ProjLifecycleListDO> rspList) {
        if (CollUtil.isEmpty(rspList)) {
            return;
        }
        List<ProjReviewBaseInfo> projReviewList = getBean(ProjReviewBaseInfoService.class)
                .listByIds(rspList.stream().map(ProjLifecycleListDO::getReviewId).collect(Collectors.toSet()));
        if (!projReviewList.isEmpty()) {
            List<ContractBaseInfo> contractList = getBean(ContractBaseInfoService.class).listByProjReviewIds(projReviewList.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
            if (!contractList.isEmpty()) {
                List<PaymentBaseInfo> paymentList = getBean(PaymentBaseInfoService.class).listByContractIds(contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
                //已申请
                Map<Long, AtomicLong> appliedPaymentMap = groupProjPaymentAmount(projReviewList, contractList, paymentList);
                rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
                    AtomicLong v = appliedPaymentMap.get(e.getReviewId());
                    if (null != v) {
                        e.setPaymentAmountApplied(v.get());
                    }
                });
                //已生效
                paymentList = paymentList.stream().filter(e -> CharSequenceUtil.equalsAny(e.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name())).collect(Collectors.toList());
                Map<Long, AtomicLong> effectedPaymentMap = groupProjPaymentAmount(projReviewList, contractList, paymentList);
                rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
                    AtomicLong v = effectedPaymentMap.get(e.getReviewId());
                    if (null != v) {
                        e.setPaymentAmountEffected(v.get());
                    }
                });
                //已投放，模拟成PaymentBaseInfo,调用同样的方法
                Map<Long, List<PaymentActualDetail>> paymentDetailMap = getBean(PaymentActualDetailService.class).getMapByPaymentIds(paymentList.stream()
                        .map(PaymentBaseInfo::getId).collect(Collectors.toList()));
                List<PaymentBaseInfo> mockList = new ArrayList<>();
                paymentDetailMap.forEach((k, v) -> {
                    if (CollUtil.isNotEmpty(v)) {
                        PaymentBaseInfo paymentBaseInfo = new PaymentBaseInfo();
                        paymentBaseInfo.setId(k);
                        paymentBaseInfo.setContractId(v.get(0).getContractId());
                        paymentBaseInfo.setApplyPaymentAmount(v.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum());
                        mockList.add(paymentBaseInfo);
                    }
                });
                Map<Long, AtomicLong> detailPaymentMap = groupProjPaymentAmount(projReviewList, contractList, mockList);
                rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
                    AtomicLong v = detailPaymentMap.get(e.getReviewId());
                    if (null != v) {
                        e.setPaymentAmountWrittenOff(v.get());
                    }
                });

            }
        }
    }

    private Map<Long/*立项id*/, AtomicLong> groupProjPaymentAmount(List<ProjReviewBaseInfo> projReviewList,
                                                                   List<ContractBaseInfo> contractList,
                                                                   List<PaymentBaseInfo> paymentList) {
        //
        Map<Long/*合同id*/, AtomicLong> contractPaymentTotalMap = new HashMap<>();
        paymentList.forEach(e -> {
            contractPaymentTotalMap.putIfAbsent(e.getContractId(), new AtomicLong(0L));
            contractPaymentTotalMap.get(e.getContractId()).addAndGet(Optional.ofNullable(e.getApplyPaymentAmount()).orElse(0L));
        });
        //
        Map<Long/*评审id*/, AtomicLong> reviewPaymentTotalMap = new HashMap<>();
        contractList.forEach(e -> {
            reviewPaymentTotalMap.putIfAbsent(e.getProjReviewId(), new AtomicLong(0L));
            if (contractPaymentTotalMap.containsKey(e.getId())) {
                reviewPaymentTotalMap.get(e.getProjReviewId()).addAndGet(contractPaymentTotalMap.get(e.getId()).get());
            }
        });
        return reviewPaymentTotalMap;
    }

    private void fillRemainingPrincipal(List<ProjLifecycleListDO> rspList) {
        if (CollUtil.isEmpty(rspList)) {
            return;
        }
        List<ProjReviewBaseInfo> projReviewList = getBean(ProjReviewBaseInfoService.class)
                .listByIds(rspList.stream().map(ProjLifecycleListDO::getReviewId).collect(Collectors.toSet()));
        if (!projReviewList.isEmpty()) {
            List<ContractBaseInfo> contractList = getBean(ContractBaseInfoService.class).listByProjReviewIds(projReviewList.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
            if (!contractList.isEmpty()) {
                List<CollectionBaseInfo> collectionList = getBean(CollectionBaseInfoService.class).listByContractIds(contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
                Map</*合同id*/Long, AtomicLong> contractPrincipalTotalMap = new HashMap<>();
                collectionList.forEach(e -> {
                    contractPrincipalTotalMap.putIfAbsent(e.getContractId(), new AtomicLong(0L));
                    long remainingPrincipal = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L) - Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                    if (remainingPrincipal < 0) {
                        remainingPrincipal = 0L;
                    }
                    contractPrincipalTotalMap.get(e.getContractId()).addAndGet(remainingPrincipal);
                });
                //
                //
                Map<Long/*评审id*/, AtomicLong> reviewPrincipalTotalMap = new HashMap<>();
                contractList.forEach(e -> {
                    reviewPrincipalTotalMap.putIfAbsent(e.getProjReviewId(), new AtomicLong(0L));
                    if (contractPrincipalTotalMap.containsKey(e.getId())) {
                        reviewPrincipalTotalMap.get(e.getProjReviewId()).addAndGet(contractPrincipalTotalMap.get(e.getId()).get());
                    }
                });
                //
                rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
                    AtomicLong v = reviewPrincipalTotalMap.get(e.getReviewId());
                    if (null != v) {
                        e.setRemainingPrincipal(v.get());
                    }
                });
            }
        }
    }


    /**
     * 以下代码将数据查入到内存中进行操作；数据量变大后需要考虑重新设计
     */
    public ProjStageTotalRSP countProj() {
        ProjStageTotalRSP result = new ProjStageTotalRSP();
        //一、数据查询
        //1、立项数据；包括普通立项和集团授信立项；处在授信立项和授信评审时都属于项目的立项阶段
        List<ProjEstablishBaseInfo> allProjEstablish = getBean(ProjEstablishBaseInfoService.class).list(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .notIn(ProjEstablishBaseInfo::getProjEstablishStatus, ListUtil.toList(CLOSED.name(), RecordStatus.EXPIRE.name()))
        );
        //2、评审数据；
        List<ProjReviewBaseInfo> allProjReview = getBean(ProjReviewBaseInfoService.class).list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, CLOSED.name(), RecordStatus.EXPIRE.name())
        );
        //3、合同数据
        List<ContractBaseInfo> allContract = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .ne(ContractBaseInfo::getContractStatus, INVALID.name())
        );

        //二、结果统计
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
        //1、总项目数；集团授信的项目从评审开始统计。
        ProjStageTotalRSP.Stage projTotal = new ProjStageTotalRSP.Stage();
        List<ProjReviewBaseInfo> groupCreditReviewList = allProjReview.stream().filter(e -> e.getGroupCreditReviewId() != null).collect(Collectors.toList());
        projTotal.setAllTotal((long) (allProjEstablish.size() + groupCreditReviewList.size()));
        projTotal.setCurrentMonthAdded(
                allProjEstablish.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count() +
                        groupCreditReviewList.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count());
        //2、立项阶段；存在立项、不存在评审的数据；不考虑集团授信项目
        ProjStageTotalRSP.Stage projEstablishTotal = new ProjStageTotalRSP.Stage();
        //基础立项
        Set<Long> alreadyReviewEstablishIdList = allProjReview.stream().map(ProjReviewBaseInfo::getProjEstablishId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<ProjEstablishBaseInfo> notReviewEstablishList = allProjEstablish.stream().filter(e -> !alreadyReviewEstablishIdList.contains(e.getId())).collect(Collectors.toList());
        //授信立项
        /*Set<Long> alreadyProjReviewGroupCreditReviewIdList = allProjReview.stream().map(ProjReviewBaseInfo::getGroupCreditReviewId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<GroupCreditReviewBaseInfo> alreadyReviewGroupCreditReviewList = groupCreditReviewList.stream().filter(e -> alreadyProjReviewGroupCreditReviewIdList.contains(e.getId())).collect(Collectors.toList());
        Set<Long> alreadyReviewGroupCreditEstablishIdList = alreadyReviewGroupCreditReviewList.stream().map(GroupCreditReviewBaseInfo::getGroupCreditEstablishId).collect(Collectors.toSet());
        List<GroupCreditEstablishBaseInfo> notReviewGroupCreditEstablishList = allGroupCreditEstablish.stream().filter(e -> !alreadyReviewGroupCreditEstablishIdList.contains(e.getId())).collect(Collectors.toList());
        */
        projEstablishTotal.setAllTotal((long) (notReviewEstablishList.size()));
        projEstablishTotal.setCurrentMonthAdded(notReviewEstablishList.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count());
        //3、评审阶段；存在评审，但是不存在合同的数据
        ProjStageTotalRSP.Stage projReviewTotal = new ProjStageTotalRSP.Stage();
        //基础立项
        Set<Long> alreadyContractReviewIdSet = allContract.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
        List<ProjReviewBaseInfo> notContractReviewList = allProjReview.stream().filter(e -> !alreadyContractReviewIdSet.contains(e.getId())).collect(Collectors.toList());
        projReviewTotal.setAllTotal((long) notContractReviewList.size());
        projReviewTotal.setCurrentMonthAdded(notContractReviewList.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count());
        //4、合同与投放阶段；非结清的合同
        ProjStageTotalRSP.Stage contractTotal = new ProjStageTotalRSP.Stage();
        List<ContractBaseInfo> notSettledContract = allContract.stream().filter(e -> !SETTLE.name().equals(e.getContractStatus())).collect(Collectors.toList());
        contractTotal.setAllTotal(notSettledContract.stream().map(ContractBaseInfo::getProjReviewId).distinct().count());
        contractTotal.setCurrentMonthAdded(notSettledContract.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).map(ContractBaseInfo::getProjReviewId).distinct().count());
        //5、结清阶段
        ProjStageTotalRSP.Stage settledTotal = new ProjStageTotalRSP.Stage();
        Map<Long, List<ContractBaseInfo>> map = allContract.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        AtomicLong settledProjTotal = new AtomicLong(0L);
        map.forEach((k, v) -> {
            if (v.stream().allMatch(e -> e.getContractStatus().equals(SETTLE.name()))) {
                settledProjTotal.incrementAndGet();
            }
        });
        settledTotal.setAllTotal(settledProjTotal.get());
        //
        List<ContractBaseInfo> currentMonthSettledContract = allContract.stream().filter(e -> e.getUpdateTime().isAfter(monthStart)).collect(Collectors.toList());
        map = currentMonthSettledContract.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        AtomicLong currentMonthSettledProjTotal = new AtomicLong(0L);
        map.forEach((k, v) -> {
            if (v.stream().allMatch(e -> e.getContractStatus().equals(SETTLE.name()))) {
                currentMonthSettledProjTotal.incrementAndGet();
            }
        });
        settledTotal.setCurrentMonthAdded(currentMonthSettledProjTotal.get());

        //三、结果填充
        result.setProjTotal(projTotal);
        result.setProjEstablishTotal(projEstablishTotal);
        result.setProjReviewTotal(projReviewTotal);
        result.setContractTotal(contractTotal);
        result.setSettledTotal(settledTotal);
        return result;

    }

}
