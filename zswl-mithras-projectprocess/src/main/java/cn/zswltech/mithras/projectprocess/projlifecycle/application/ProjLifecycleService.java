package cn.zswltech.mithras.projectprocess.projlifecycle.application;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.projlifecycle.ProjStageTotalRSP;
import cn.zswltech.mithras.dto.projlifecycle.ProjectLifecycleListREQ;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.model.ProjLifecycleSnapshot;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.ProjLifecycleEventMapper;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.model.ProjLifecycleListDO;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author yibin
 */
@Service
public class ProjLifecycleService {

    private static final String CONTRACT_STATUS_INVALID = "INVALID";
    private static final String CONTRACT_STATUS_NEW = "NEW";
    private static final String CONTRACT_STATUS_SETTLE = "SETTLE";
    private static final String PROJ_ITEM_STATUS_CLOSED = "CLOSED";
    private static final String PAYMENT_STATUS_TAKE_EFFECT = "TAKE_EFFECT";
    private static final String PAYMENT_STATUS_FINISHED = "FINISHED";

    @Resource
    private ProjLifecycleAuthPort authPort;
    @Resource
    private ProjLifecycleDataPort dataPort;
    @Resource
    private ProjLifecycleEventMapper projLifecycleEventMapper;

    public Page<ProjLifecycleListDO> list(ProjectLifecycleListREQ req) {
        List<Long> deptIds = authPort.canViewDeptIds();
        if (deptIds != null && deptIds.isEmpty()) {
            req.setSponsorId(authPort.currentUserId());
        }
        req.setDeptIdList(deptIds);
        Page<ProjLifecycleListDO> pageData = projLifecycleEventMapper.mainList(new Page<>(req.getPage(), req.getPageSize()), req);
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
        List<ProjLifecycleSnapshot.ProjReview> projReviewList = dataPort.listReviewByIds(reviewIds(rspList));
        if (!projReviewList.isEmpty()) {
            List<ProjLifecycleSnapshot.Contract> contractList = dataPort.listContractsByReviewIds(projReviewList.stream().map(ProjLifecycleSnapshot.ProjReview::getId).collect(Collectors.toList()));
            Map<Long, AtomicLong> groupValueMap = groupProjContractAmount(contractList);
            rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> setIfPresent(e, groupValueMap.get(e.getReviewId()), true));
            contractList = contractList.stream().filter(e -> !StrUtil.equals(e.getContractStatus(), CONTRACT_STATUS_NEW)).collect(Collectors.toList());
            Map<Long, AtomicLong> effectedGroupValueMap = groupProjContractAmount(contractList);
            rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
                AtomicLong v = effectedGroupValueMap.get(e.getReviewId());
                if (v != null) {
                    e.setContractAmountEffected(v.get());
                }
            });
        }
    }

    private void setIfPresent(ProjLifecycleListDO row, AtomicLong value, boolean applied) {
        if (value == null) {
            return;
        }
        if (applied) {
            row.setContractAmountApplied(value.get());
        }
    }

    private Collection<Long> reviewIds(List<ProjLifecycleListDO> rows) {
        return rows.stream().map(ProjLifecycleListDO::getReviewId).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private Map<Long, AtomicLong> groupProjContractAmount(List<ProjLifecycleSnapshot.Contract> contractList) {
        List<Long> contractIds = contractList.stream().map(ProjLifecycleSnapshot.Contract::getId).collect(Collectors.toList());
        List<ProjLifecycleSnapshot.ContractPrice> leasePriceList = dataPort.listLeasePricesByContractIds(contractIds);
        List<ProjLifecycleSnapshot.ContractPrice> aocPriceList = dataPort.listAocPricesByContractIds(contractIds);
        List<ProjLifecycleSnapshot.ContractPrice> factoringPriceList = dataPort.listFactoringPricesByContractIds(contractIds);
        Map<Long, AtomicLong> contractTotalMap = new HashMap<>();
        leasePriceList.forEach(e -> add(contractTotalMap, e.getContractId(), e.getAmount()));
        aocPriceList.forEach(e -> add(contractTotalMap, e.getContractId(), e.getAmount()));
        factoringPriceList.forEach(e -> add(contractTotalMap, e.getContractId(), e.getAmount()));
        Map<Long, AtomicLong> projReviewTotalMap = new HashMap<>();
        contractList.forEach(e -> {
            projReviewTotalMap.putIfAbsent(e.getProjReviewId(), new AtomicLong(0L));
            if (contractTotalMap.containsKey(e.getId())) {
                projReviewTotalMap.get(e.getProjReviewId()).addAndGet(contractTotalMap.get(e.getId()).get());
            }
        });
        return projReviewTotalMap;
    }

    private void add(Map<Long, AtomicLong> amountMap, Long key, Long amount) {
        amountMap.putIfAbsent(key, new AtomicLong(0L));
        amountMap.get(key).addAndGet(Optional.ofNullable(amount).orElse(0L));
    }

    private void fillPaymentAmount(List<ProjLifecycleListDO> rspList) {
        if (CollUtil.isEmpty(rspList)) {
            return;
        }
        List<ProjLifecycleSnapshot.ProjReview> projReviewList = dataPort.listReviewByIds(reviewIds(rspList));
        if (projReviewList.isEmpty()) {
            return;
        }
        List<ProjLifecycleSnapshot.Contract> contractList = dataPort.listContractsByReviewIds(projReviewList.stream().map(ProjLifecycleSnapshot.ProjReview::getId).collect(Collectors.toList()));
        if (contractList.isEmpty()) {
            return;
        }
        List<ProjLifecycleSnapshot.Payment> paymentList = dataPort.listPaymentsByContractIds(contractList.stream().map(ProjLifecycleSnapshot.Contract::getId).collect(Collectors.toList()));
        Map<Long, AtomicLong> appliedPaymentMap = groupProjPaymentAmount(contractList, paymentList);
        rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
            AtomicLong v = appliedPaymentMap.get(e.getReviewId());
            if (v != null) {
                e.setPaymentAmountApplied(v.get());
            }
        });
        paymentList = paymentList.stream()
                .filter(e -> CharSequenceUtil.equalsAny(e.getPaymentStatus(), PAYMENT_STATUS_TAKE_EFFECT, PAYMENT_STATUS_FINISHED))
                .collect(Collectors.toList());
        Map<Long, AtomicLong> effectedPaymentMap = groupProjPaymentAmount(contractList, paymentList);
        rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
            AtomicLong v = effectedPaymentMap.get(e.getReviewId());
            if (v != null) {
                e.setPaymentAmountEffected(v.get());
            }
        });
        Map<Long, List<ProjLifecycleSnapshot.PaymentActualDetail>> paymentDetailMap = dataPort.getPaymentActualDetailsByPaymentIds(paymentList.stream()
                .map(ProjLifecycleSnapshot.Payment::getId).collect(Collectors.toList()));
        List<ProjLifecycleSnapshot.Payment> mockList = new ArrayList<>();
        paymentDetailMap.forEach((k, v) -> {
            if (CollUtil.isNotEmpty(v)) {
                ProjLifecycleSnapshot.Payment payment = new ProjLifecycleSnapshot.Payment();
                payment.setId(k);
                payment.setContractId(v.get(0).getContractId());
                payment.setApplyPaymentAmount(v.stream().mapToLong(e -> Optional.ofNullable(e.getPaidInAmount()).orElse(0L)).sum());
                mockList.add(payment);
            }
        });
        Map<Long, AtomicLong> detailPaymentMap = groupProjPaymentAmount(contractList, mockList);
        rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
            AtomicLong v = detailPaymentMap.get(e.getReviewId());
            if (v != null) {
                e.setPaymentAmountWrittenOff(v.get());
            }
        });
    }

    private Map<Long, AtomicLong> groupProjPaymentAmount(List<ProjLifecycleSnapshot.Contract> contractList, List<ProjLifecycleSnapshot.Payment> paymentList) {
        Map<Long, AtomicLong> contractPaymentTotalMap = new HashMap<>();
        paymentList.forEach(e -> add(contractPaymentTotalMap, e.getContractId(), e.getApplyPaymentAmount()));
        Map<Long, AtomicLong> reviewPaymentTotalMap = new HashMap<>();
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
        List<ProjLifecycleSnapshot.ProjReview> projReviewList = dataPort.listReviewByIds(reviewIds(rspList));
        if (projReviewList.isEmpty()) {
            return;
        }
        List<ProjLifecycleSnapshot.Contract> contractList = dataPort.listContractsByReviewIds(projReviewList.stream().map(ProjLifecycleSnapshot.ProjReview::getId).collect(Collectors.toList()));
        if (contractList.isEmpty()) {
            return;
        }
        List<ProjLifecycleSnapshot.Collection> collectionList = dataPort.listCollectionsByContractIds(contractList.stream().map(ProjLifecycleSnapshot.Contract::getId).collect(Collectors.toList()));
        Map<Long, AtomicLong> contractPrincipalTotalMap = new HashMap<>();
        collectionList.forEach(e -> {
            contractPrincipalTotalMap.putIfAbsent(e.getContractId(), new AtomicLong(0L));
            long remainingPrincipal = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L) - Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
            contractPrincipalTotalMap.get(e.getContractId()).addAndGet(Math.max(remainingPrincipal, 0L));
        });
        Map<Long, AtomicLong> reviewPrincipalTotalMap = new HashMap<>();
        contractList.forEach(e -> {
            reviewPrincipalTotalMap.putIfAbsent(e.getProjReviewId(), new AtomicLong(0L));
            if (contractPrincipalTotalMap.containsKey(e.getId())) {
                reviewPrincipalTotalMap.get(e.getProjReviewId()).addAndGet(contractPrincipalTotalMap.get(e.getId()).get());
            }
        });
        rspList.stream().filter(e -> e.getReviewId() != null).forEach(e -> {
            AtomicLong v = reviewPrincipalTotalMap.get(e.getReviewId());
            if (v != null) {
                e.setRemainingPrincipal(v.get());
            }
        });
    }

    public ProjStageTotalRSP countProj() {
        ProjStageTotalRSP result = new ProjStageTotalRSP();
        List<ProjLifecycleSnapshot.ProjEstablish> allProjEstablish = dataPort.listAllEstablish().stream()
                .filter(e -> !ListUtil.toList(PROJ_ITEM_STATUS_CLOSED, RecordStatus.EXPIRE.name()).contains(e.getProjEstablishStatus()))
                .collect(Collectors.toList());
        List<ProjLifecycleSnapshot.ProjReview> allProjReview = dataPort.listAllReview().stream()
                .filter(e -> !ListUtil.toList(PROJ_ITEM_STATUS_CLOSED, RecordStatus.EXPIRE.name()).contains(e.getProjReviewStatus()))
                .collect(Collectors.toList());
        List<ProjLifecycleSnapshot.Contract> allContract = dataPort.listAllContract().stream()
                .filter(e -> !CONTRACT_STATUS_INVALID.equals(e.getContractStatus()))
                .collect(Collectors.toList());
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
        ProjStageTotalRSP.Stage projTotal = new ProjStageTotalRSP.Stage();
        List<ProjLifecycleSnapshot.ProjReview> groupCreditReviewList = allProjReview.stream().filter(e -> e.getGroupCreditReviewId() != null).collect(Collectors.toList());
        projTotal.setAllTotal((long) (allProjEstablish.size() + groupCreditReviewList.size()));
        projTotal.setCurrentMonthAdded(allProjEstablish.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count()
                + groupCreditReviewList.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count());
        ProjStageTotalRSP.Stage projEstablishTotal = new ProjStageTotalRSP.Stage();
        Set<Long> alreadyReviewEstablishIdList = allProjReview.stream().map(ProjLifecycleSnapshot.ProjReview::getProjEstablishId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<ProjLifecycleSnapshot.ProjEstablish> notReviewEstablishList = allProjEstablish.stream().filter(e -> !alreadyReviewEstablishIdList.contains(e.getId())).collect(Collectors.toList());
        projEstablishTotal.setAllTotal((long) notReviewEstablishList.size());
        projEstablishTotal.setCurrentMonthAdded(notReviewEstablishList.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count());
        ProjStageTotalRSP.Stage projReviewTotal = new ProjStageTotalRSP.Stage();
        Set<Long> alreadyContractReviewIdSet = allContract.stream().map(ProjLifecycleSnapshot.Contract::getProjReviewId).collect(Collectors.toSet());
        List<ProjLifecycleSnapshot.ProjReview> notContractReviewList = allProjReview.stream().filter(e -> !alreadyContractReviewIdSet.contains(e.getId())).collect(Collectors.toList());
        projReviewTotal.setAllTotal((long) notContractReviewList.size());
        projReviewTotal.setCurrentMonthAdded(notContractReviewList.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).count());
        ProjStageTotalRSP.Stage contractTotal = new ProjStageTotalRSP.Stage();
        List<ProjLifecycleSnapshot.Contract> notSettledContract = allContract.stream().filter(e -> !CONTRACT_STATUS_SETTLE.equals(e.getContractStatus())).collect(Collectors.toList());
        contractTotal.setAllTotal(notSettledContract.stream().map(ProjLifecycleSnapshot.Contract::getProjReviewId).distinct().count());
        contractTotal.setCurrentMonthAdded(notSettledContract.stream().filter(e -> e.getCreateTime().isAfter(monthStart)).map(ProjLifecycleSnapshot.Contract::getProjReviewId).distinct().count());
        ProjStageTotalRSP.Stage settledTotal = new ProjStageTotalRSP.Stage();
        Map<Long, List<ProjLifecycleSnapshot.Contract>> map = allContract.stream().collect(Collectors.groupingBy(ProjLifecycleSnapshot.Contract::getProjReviewId));
        AtomicLong settledProjTotal = new AtomicLong(0L);
        map.forEach((k, v) -> {
            if (v.stream().allMatch(e -> e.getContractStatus().equals(CONTRACT_STATUS_SETTLE))) {
                settledProjTotal.incrementAndGet();
            }
        });
        settledTotal.setAllTotal(settledProjTotal.get());
        Map<Long, List<ProjLifecycleSnapshot.Contract>> currentMonthSettledMap = allContract.stream()
                .filter(e -> e.getUpdateTime().isAfter(monthStart))
                .collect(Collectors.groupingBy(ProjLifecycleSnapshot.Contract::getProjReviewId));
        AtomicLong currentMonthSettledProjTotal = new AtomicLong(0L);
        currentMonthSettledMap.forEach((k, v) -> {
            if (v.stream().allMatch(e -> e.getContractStatus().equals(CONTRACT_STATUS_SETTLE))) {
                currentMonthSettledProjTotal.incrementAndGet();
            }
        });
        settledTotal.setCurrentMonthAdded(currentMonthSettledProjTotal.get());
        result.setProjTotal(projTotal);
        result.setProjEstablishTotal(projEstablishTotal);
        result.setProjReviewTotal(projReviewTotal);
        result.setContractTotal(contractTotal);
        result.setSettledTotal(settledTotal);
        return result;
    }
}
