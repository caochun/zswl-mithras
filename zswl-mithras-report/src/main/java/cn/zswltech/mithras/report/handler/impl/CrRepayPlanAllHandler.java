package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.CrActualRepayDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrRepayPlan;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * 征信报送-还款计划表
 *
 * @author wangchuanhao
 * @date 2022/10/8 6:42 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrRepayPlanAllHandler extends CrAbstractHandler<CrRepayPlanDraft, CrRepayPlan> {

    @Value("${report.overdue.days}")
    private Integer overdueDays;

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractRentActualLibMapper contractRentActualLibMapper;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;
    @Resource
    private CrActualRepayDraftMapper crActualRepayDraftMapper;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private CrRepayPlanDraftService crRepayPlanDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.REPAY_PLAN_ALL;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<PaymentBaseInfo> paymentBaseInfos = new ArrayList<>();
        //筛选系统内合同状态≠结清/作废，业务类型=租赁/转租赁/保理的合同
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.INVALID.name())
                .ne(ContractBaseInfo::getBizType, ProjectBizType.ZR.name())
        );
        if (CollUtil.isEmpty(contractBaseInfoList)) {
            log.info("系统内合同状态≠结清/作废，业务类型=租赁/转租赁/保理的合同需要处理的数据为空");
            return;
        }
        Set<Long> contractIdList = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        log.info("系统内合同状态≠结清/作废，业务类型=租赁/转租赁/保理的合同需要处理的数据contractIds：[{}]", contractIdList);
        //获取合同最新版本
        List<ContractBaseInfoLib> contractBaseInfoLibs = contractBaseInfoLibMapper.listNewestContractByContractIds(contractIdList);

        if (CollUtil.isEmpty(contractBaseInfoLibs)) {
            return;
        }

        Set<Long> receiptIdSet = new HashSet<>();
        //修复单个合同多个借据只能取到一个借据的bug
        contractBaseInfoLibs.forEach(contractBaseInfoLib -> {
            List<ContractRentActualLib> contractRentActualLibs = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
                    .eq(ContractRentActualLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractRentActualLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                    .ne(ContractRentActualLib::getCashFlowCode, "")
                    .isNotNull(ContractRentActualLib::getCashFlowCode));
            if (CollUtil.isNotEmpty(contractBaseInfoLibs)) {
                receiptIdSet.addAll(contractRentActualLibs.stream().map(ContractRentActual::getReceiptId).collect(Collectors.toSet()));
            }
        });

        if (CollUtil.isNotEmpty(receiptIdSet)) {
            paymentBaseInfos.addAll(paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .in(PaymentBaseInfo::getReceiptIdFinal, receiptIdSet)));
        }

        paymentBaseInfos = paymentBaseInfos.stream().distinct().collect(Collectors.toList());

        if (CollUtil.isEmpty(paymentBaseInfos)) {
            return;
        }

        //提前查询数据库，提升效率
        Set<Long> paymentIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        Set<Long> receiptIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet());
        Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountDraft::getPaymentId, paymentIds)
        ).stream().collect(Collectors.groupingBy(CrAccountDraft::getPaymentId));

        Map<Long, List<CrRepayPlanDraft>> paymentIdRepayPlanMap = crRepayPlanDraftService.list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                        .in(CrRepayPlanDraft::getPaymentId, paymentIds))
                .stream().collect(Collectors.groupingBy(CrRepayPlanDraft::getPaymentId));
        Map<Long, List<ContractReceiptLib>> paymentIdReceiptListMap = contractReceiptLibMapper.selectList(Wrappers.<ContractReceiptLib>lambdaQuery()
                .in(ContractReceiptLib::getOriginId, receiptIds)
        ).stream().collect(Collectors.groupingBy(ContractReceiptLib::getOriginId));

        paymentBaseInfos.forEach(paymentBaseInfo -> {
            log.info("付款申请的合同ID为[{}]", paymentBaseInfo.getContractId());
            //拿到当前付款申请的所有账户
            List<CrAccountDraft> accountDrafts = paymentIdAccountListMap.get(paymentBaseInfo.getId());
            if (CollUtil.isEmpty(accountDrafts) || Objects.isNull(paymentBaseInfo.getReceiptIdFinal())) {
                //还没有账户
                return;
            }
            List<ContractReceiptLib> contractReceiptLibs = paymentIdReceiptListMap.get(paymentBaseInfo.getReceiptIdFinal());
            contractReceiptLibs.sort(Comparator.comparing(ContractReceiptLib::getVersion).reversed());
            ContractReceiptLib contractReceiptLib = contractReceiptLibs.get(0);
            //查询实际租金表并根据期项映射
            List<ContractRentActualLib> contractRentActualLibs = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
                            .eq(ContractRentActualLib::getContractId, paymentBaseInfo.getContractId())
                            .eq(ContractRentActualLib::getReceiptId, contractReceiptLib.getOriginId())
                            .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                            .eq(ContractRentActualLib::getVersion, contractReceiptLib.getVersion())
//                    .gt(ContractRentActualLib::getCreateTime, lastDealTime)
//                    .le(ContractRentActualLib::getCreateTime, dealTime)
                            .ne(ContractRentActualLib::getCashFlowCode, "")
                            .isNotNull(ContractRentActualLib::getCashFlowCode)
            );
            if (CollUtil.isEmpty(contractRentActualLibs)) {
                //当前时间段没有新的租金表生成
                return;
            }
            //需要将首期利息加到租金表的第0期，这个是征信特有的功能，先查询是否有首期利息
            CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .eq(CollectionBaseInfo::getPhase, 0)
                    // 因为首期利息是借据维度的，所以需要加上下面这个条件
                    .eq(CollectionBaseInfo::getReceiptId, contractReceiptLib.getOriginId())
                    .eq(CollectionBaseInfo::getContractId, paymentBaseInfo.getContractId()));
            //如果存在就加入
            if (Objects.nonNull(collectionBaseInfo)) {
                ContractRentActualLib firstInterest = new ContractRentActualLib();
                firstInterest.setContractId(collectionBaseInfo.getContractId());
                firstInterest.setPrincipal(0L);
                firstInterest.setRent(collectionBaseInfo.getPlanCollectionAmount());
                firstInterest.setInterest(collectionBaseInfo.getInterest());
                firstInterest.setCashFlowPhase(collectionBaseInfo.getPhase());
                firstInterest.setCashFlowDate(collectionBaseInfo.getPlanCollectionDate());
                contractRentActualLibs.add(firstInterest);
            }

            Map<Integer, ContractRentActualLib> integerContractRentActualLibMap = contractRentActualLibs
                    .stream().collect(Collectors.toMap(ContractRentActualLib::getCashFlowPhase, Function.identity(), (k1, k2) -> k1));

            //根据账户查询是否有还款表，有还款表的话直接更新覆盖，没有的直接走新增逻辑
            List<CrRepayPlanDraft> crRepayPlanDrafts = paymentIdRepayPlanMap.get(paymentBaseInfo.getId());
            //当前付款申请的所有金额之和，后面分表更新需要根据这个值计算比例
            long sum = accountDrafts.stream().mapToLong(CrAccountDraft::getPaymentAmount).sum();
            if (sum == 0) {
                return;
            }
            if (CollUtil.isEmpty(crRepayPlanDrafts)) {
                //还没有还款表，需要进行新增
                List<CrRepayPlanDraft> needInsertList = new LinkedList<>();
                accountDrafts.sort(Comparator.comparing(CrAccountDraft::getPaymentApplyCode));
                int m=0;
                Map<Long,Long> accountDraftsMap = new HashMap<>();
                accountDrafts.forEach(accountDraft -> {
                    accountDraftsMap.put(accountDraft.getId(),0l);
                });
                for (ContractRentActualLib actualLib : integerContractRentActualLibMap.values()) {
                    long totalRent = 0L;
                    long totalPrincipal = 0L;
                    for (int i = 0; i < accountDrafts.size(); i++) {
                        CrAccountDraft accountDraft = accountDrafts.get(i);
                        BigDecimal decimal = BigDecimal.valueOf(sum);
                        long principal = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(Optional.ofNullable(actualLib.getPrincipal()).orElse(0L))
                                .multiply(BigDecimal.valueOf(accountDraft.getPaymentAmount()))
                                .divide(decimal, 2, RoundingMode.HALF_UP).longValue());
                        long rent = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(Optional.ofNullable(actualLib.getRent()).orElse(0L))
                                .multiply(BigDecimal.valueOf(accountDraft.getPaymentAmount()))
                                .divide(decimal, 2, RoundingMode.HALF_UP).longValue());
                        if(m == integerContractRentActualLibMap.values().size()-1){//最后一笔
                            principal = accountDraft.getPaymentAmount() - accountDraftsMap.get(accountDraft.getId());
                        }
                        accountDraftsMap.put(accountDraft.getId(),accountDraftsMap.get(accountDraft.getId())+ principal);
                        if (i == accountDrafts.size() - 1) {
                            principal = Optional.ofNullable(actualLib.getPrincipal()).orElse(0L) - totalPrincipal;
                            rent = Optional.ofNullable(actualLib.getRent()).orElse(0L) - totalRent;
                        }
                        CrRepayPlanDraft draft = buildCrRepayPlan(paymentBaseInfo, rent, principal, actualLib, accountDraft);
                        //1、增加筛选条件：对其项下各期次的计划收款日期<=当前日期
                        // &核销状态=未核销/部分核销/核销完毕（且在待报送还款表内不存在）
                        // &该期次数据在逾期表内不存在的数据更新至待报送-还款表内。
                        if(!reportDataRepository.getPlanIsUpate(draft,contractReceiptLib.getOriginId(),dealTime,false)){
                            draft.setIsShow(YesOrNoNumberEnum.NO.getCode());
                        }
                        needInsertList.add(draft);
                        totalPrincipal += draft.getPrincipal();
                        totalRent += draft.getRent();
                    }
                    m++;
                }
                crRepayPlanDraftService.saveBatch(needInsertList);
                return;
            }
            Map<String, List<CrRepayPlanDraft>> accountRepayPlanMap = crRepayPlanDrafts.stream()
                    .collect(Collectors.groupingBy(CrRepayPlanDraft::getPaymentApplyCode));
            //处理数据更新
            getBean(CrRepayPlanAllHandler.class).updateRepayPlanData(accountDrafts, paymentBaseInfo, accountRepayPlanMap, integerContractRentActualLibMap, sum, dealTime,contractReceiptLib.getOriginId());
        });
    }

    private CrRepayPlanDraft buildCrRepayPlan(PaymentBaseInfo paymentBaseInfo, long rent, long principal, ContractRentActualLib actualLib, CrAccountDraft accountDraft) {
        CrRepayPlanDraft build = new CrRepayPlanDraft();
        build.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
        build.setProcBusinessKey(null);
        build.setPlanPenaltyInterest(0L);
        build.setAlreadyPenaltyInterest(0L);
        build.setReportState(ReportState.TO_BE_REPORT.name());
        build.setPrincipal(principal);
        build.setPhase(actualLib.getCashFlowPhase());
        build.setRent(rent);
        build.setPaymentId(paymentBaseInfo.getId());
        build.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
        build.setCashFlowDate(actualLib.getCashFlowDate());
        build.setGracePeriod(overdueDays.toString());
        build.setBusinessKey(build.genBusinessKey());
        build.setContractId(paymentBaseInfo.getContractId());
        return build;
    }

    public void updateRepayPlanData(List<CrAccountDraft> accountDrafts, PaymentBaseInfo paymentBaseInfo, Map<String, List<CrRepayPlanDraft>> accountRepayPlanMap,
                                    Map<Integer, ContractRentActualLib> integerContractRentActualLibMap, long sum, LocalDateTime dealTime, Long receiptId) {
        //这里的排序，不能按照编号排序，可能这里有问题
        accountDrafts.sort(Comparator.comparing(CrAccountDraft::getPaymentAmount));
        List<CrRepayPlanDraft> resList = new LinkedList<>();
        for (Map.Entry<Integer, ContractRentActualLib> entry : integerContractRentActualLibMap.entrySet()) {
            ContractRentActualLib actualLib = entry.getValue();
            long totalRent = 0L;
            long totalPrincipal = 0L;
            for (int i = 0; i < accountDrafts.size(); i++) {
                CrAccountDraft accountDraft = accountDrafts.get(i);
                //计算本金和租金
                BigDecimal amount = BigDecimal.valueOf(accountDraft.getPaymentAmount());
                BigDecimal totalAmount = BigDecimal.valueOf(sum);
                BigDecimal currentPrincipal = BigDecimal.valueOf(Optional.ofNullable(actualLib.getPrincipal()).orElse(0L));
                BigDecimal actualRent = BigDecimal.valueOf(Optional.ofNullable(actualLib.getRent()).orElse(0L));

                long principal = Util.mithrasLongDecimalTwo(amount.divide(totalAmount, 10, RoundingMode.HALF_UP).multiply(currentPrincipal).longValue());
                long rent = Util.mithrasLongDecimalTwo(amount.divide(totalAmount, 10, RoundingMode.HALF_UP).multiply(actualRent).longValue());
                if (i == accountDrafts.size() - 1) {
                    principal = Optional.ofNullable(actualLib.getPrincipal()).orElse(0L) - totalPrincipal;
                    rent = Optional.ofNullable(actualLib.getRent()).orElse(0L) - totalRent;
                }
                //当前账户的所有现有当前期项
                Map<Integer, CrRepayPlanDraft> phasePlanMap = accountRepayPlanMap.get(accountDraft.getPaymentApplyCode())
                        .stream().collect(Collectors.toMap(CrRepayPlanDraft::getPhase, Function.identity(),
                                (err1, err2) -> {
                                    throw new MithrasException("期项重复，数据映射失败！");
                                }));
                CrRepayPlanDraft existPlan = phasePlanMap.get(entry.getKey());
                if (Objects.nonNull(existPlan)) {
                    //更新
                    existPlan.setRent(rent);
                    existPlan.setPrincipal(principal);
                    existPlan.setReportState(ReportState.TO_BE_REPORT.name());
                    existPlan.setCashFlowDate(actualLib.getCashFlowDate());
                    //是否更新
                    if(reportDataRepository.getPlanIsUpate(existPlan,receiptId,dealTime,true)){
                        existPlan.setIsShow(YesOrNoNumberEnum.YES.getCode());
                        existPlan.setUpdateTime(LocalDateTime.now());
                        resList.add(existPlan);
                    }
                    totalRent += rent;
                    totalPrincipal += principal;
                } else {
                    //现有数据不存在，说明是展期数据，需要新增
                    CrRepayPlanDraft draft = buildCrRepayPlan(paymentBaseInfo, rent, principal, actualLib, accountDraft);
                    draft.setIsShow(YesOrNoNumberEnum.YES.getCode());
                    totalRent += draft.getRent();
                    totalPrincipal += draft.getPrincipal();
                    //是否更新
                    if(reportDataRepository.getPlanIsUpate(draft,receiptId,dealTime,false)){
                        resList.add(draft);
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(resList)) {
            getBean(CrRepayPlanDraftService.class).saveOrUpdateBatch(resList);
        }
    }

    @Override
    public Integer sort() {
        return 75;
    }
}
