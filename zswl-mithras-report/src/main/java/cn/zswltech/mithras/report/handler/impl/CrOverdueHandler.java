package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.handler.ReportDataRepository;
import cn.zswltech.mithras.report.mapper.draft.CrOverdueRecordDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrOverdueRecord;
import cn.zswltech.mithras.report.service.draft.CrOverdueRecordDraftService;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-逾期信息表
 * 在账户表之后执行
 *
 * @author wangchuanhao
 * @date 2022/10/9 1:46 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrOverdueHandler extends CrAbstractHandler<CrOverdueRecordDraft, CrOverdueRecord> {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ReportDataRepository reportDataRepository;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CrOverdueRecordDraftMapper crOverdueRecordDraftMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private CrRepayPlanDraftService repayPlanDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.OVERDUE_RECORD;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        try {
            SpringContextHolder.getBean(CrOverdueHandler.class).handle(dealTime, lastDealTime);
        } catch (Exception e) {
            log.error("直租逾期处理错误!", e);
        }
    }

    public void handle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        // 历史的还处于逾期状态的数据，用于判断是否处理成还清
        List<CrOverdueRecordDraft> lastDataList = crOverdueRecordDraftMapper.queryNotFinishHistoryList();
        //过滤非直租
        lastDataList = lastDataList.stream().filter(a -> a.getPaymentApplyCode().contains("HZ")).collect(Collectors.toList());
        // 用来判断修改还是新增
        List<CrOverdueRecordDraft> existDataList = draftMapper.selectList(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .eq(CrOverdueRecordDraft::getGenDate, dealTime.toLocalDate())
        );
        Map<String, CrOverdueRecordDraft> existDataMap = existDataList.stream().collect(Collectors.toMap(CrOverdueRecordDraft::getBusinessKey, Function.identity()));
        // 付款id集合 用来判断是否需要填写变更日期
        Map<Long, CrOverdueRecordDraft> lastDataPaymentIdMap = lastDataList.stream().collect(Collectors.toMap(CrOverdueRecordDraft::getPaymentId, Function.identity()));
        // 寻找此次需要处理的逾期
        List<CollectionBaseInfo> overdueCollectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED)
                .ne(CollectionBaseInfo::getPhase, 0)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .le(CollectionBaseInfo::getPlanCollectionDate, dealTime)
        );

        overdueCollectionBaseInfoList = overdueCollectionBaseInfoList.stream().filter(a -> LongUtil.null2zero(a.getCollectionAmount()) < LongUtil.null2zero(a.getPlanCollectionAmount()))
                .collect(Collectors.toList());
        //现在没有逾期了，需要将征信现存的逾期处理成非逾期
        if (CollUtil.isEmpty(overdueCollectionBaseInfoList) && CollUtil.isNotEmpty(lastDataList)) {
            Map<Long, CrOverdueRecordDraft> map = lastDataList.stream().collect(Collectors.toMap(CrOverdueRecordDraft::getPaymentId, Function.identity()));
            //这里需要过滤非直租合同
            List<Long> contractIds = contractBaseInfoService.listByIds(lastDataList.stream().map(CrOverdueRecordDraft::getContractId).collect(Collectors.toList()))
                    .stream().filter(a -> LeaseType.zhi_zu.name().equals(a.getLeaseType()))
                    .map(ContractBaseInfo::getId).collect(Collectors.toList());
            lastDataList = lastDataList.stream().filter(a -> contractIds.contains(a.getContractId())).collect(Collectors.toList());
            List<CrOverdueRecordDraft> drafts = new LinkedList<>();
            List<Long> list = lastDataList.stream().map(CrOverdueRecordDraft::getPaymentId).collect(Collectors.toList());
            for (Long paymentId : list) {
                CrOverdueRecordDraft recordDraft = getNotOverdueRecordDraft(dealTime, map, paymentId);
                drafts.add(recordDraft);
                reportDataRepository.deletePlanForOverdue(recordDraft);
            }
            draftService.saveBatch(drafts);
            return;
        }

        //过滤掉非直租的数据
        Set<Long> contractIds = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                .in(ContractBaseInfoLib::getOriginId, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()))
        ).stream().filter(a -> LeaseType.zhi_zu.name().equals(a.getLeaseType())).map(ContractBaseInfoLib::getOriginId).collect(Collectors.toSet());

        //这里需要查询还款计划表，找出宽限期，然后做过滤
        overdueCollectionBaseInfoList = overdueCollectionBaseInfoList.stream().filter(a -> contractIds.contains(a.getContractId())).collect(Collectors.toList());
        if (CollUtil.isEmpty(overdueCollectionBaseInfoList)) {
            return;
        }
        Map<Long, Map<Integer, CrRepayPlanDraft>> planMap = new HashMap<>(8);
        Map<Long, List<CrRepayPlanDraft>> contractPlanListMap = repayPlanDraftService.list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                        .in(CrRepayPlanDraft::getContractId, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()))
                        .in(CrRepayPlanDraft::getPhase, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getPhase).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(CrRepayPlanDraft::getContractId));
        contractPlanListMap.forEach((contractId, planList) -> {
            planMap.put(contractId, planList.stream().collect(Collectors.toMap(CrRepayPlanDraft::getPhase, Function.identity())));
        });

        //中间变量，用户存储过滤后需要处理的数据
        List<CollectionBaseInfo> needHandleDataList = new ArrayList<>(overdueCollectionBaseInfoList.size());

        //由于需求变更，现在每个期项都要考虑宽限期
        overdueCollectionBaseInfoList.forEach(collectionBaseInfo -> {
            Map<Integer, CrRepayPlanDraft> integerCrRepayPlanDraftMap = planMap.get(collectionBaseInfo.getContractId());
            if (CollUtil.isEmpty(integerCrRepayPlanDraftMap)) {
                //这里为空可能是因为该合同不上报征信，导致还款计划没有数据
                log.error("合同ID为{}, 期项为{}的还款计划为空, 请业务验证数据的正确性！", collectionBaseInfo.getContractId(), collectionBaseInfo.getPhase());
                return;
            }
            CrRepayPlanDraft planDraft = integerCrRepayPlanDraftMap.get(collectionBaseInfo.getPhase());
            String gracePeriod = reportDataRepository.getGracePeriod(planDraft);
            LocalDate gracePeriodDate = dealTime.toLocalDate().minusDays(Long.parseLong(
                    // 理论上不会为null， 但是为了防止数据错误，这里做空判断
                    Objects.isNull(gracePeriod) ? "0" : gracePeriod
            ));
            if (collectionBaseInfo.getPlanCollectionDate().isBefore(gracePeriodDate)) {
                //还款时间比现在到过去宽限期的天数还要小，则认为是逾期
                needHandleDataList.add(collectionBaseInfo);
            }
        });

        if (CollUtil.isEmpty(needHandleDataList)) {
            //需要将昨天的数据报0
            if (CollUtil.isNotEmpty(lastDataList)) {
                List<CrOverdueRecordDraft> needUpdateList = new ArrayList<>(lastDataList.size());
                for (CrOverdueRecordDraft draft : lastDataList) {
                    CrOverdueRecordDraft recordDraft = BeanUtil.copyProperties(draft, CrOverdueRecordDraft.class);
                    recordDraft.setOverdueTotal(0L);
                    recordDraft.setOverduePrincipal(0L);
                    recordDraft.setOverdueDay(0);
                    recordDraft.setOverdueChangeDate(dealTime.toLocalDate().plusDays(-1));
                    recordDraft.setGenDate(dealTime.toLocalDate());
                    recordDraft.setId(null);
                    needUpdateList.add(recordDraft);
                }
                SpringContextHolder.getBean(CrOverdueRecordDraftService.class).saveBatch(needUpdateList);
            }
            return;
        }
        log.info("直租需要处理的逾期收款数据collectionIds：[{}]", needHandleDataList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()));

        StringBuilder builder = new StringBuilder();
        Map<Long, List<CollectionBaseInfo>> overdueCollectionBaseInfoMap = needHandleDataList.stream()
                .peek(a -> {
                    if (Objects.isNull(a.getReceiptId())) {
                        builder.append(a.getId()).append("、");
                    }
                })
                .filter(a -> Objects.nonNull(a.getReceiptId())).collect(Collectors.groupingBy(CollectionBaseInfo::getReceiptId));

        //receiptId 可能为空，打个日志记录一下为空的数据
        if (builder.length() > 0) {
            log.error("征信逾期抽取过滤数据, 收款基本表对应ID：{}", builder.substring(0, builder.length() - 1));
        }
        // 找到借据关联的付款
        List<PaymentBaseInfo> paymentBaseInfoList = CollectionUtils.isEmpty(needHandleDataList) ? new ArrayList<>() :
                paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getReceiptIdFinal, needHandleDataList.stream().map(CollectionBaseInfo::getReceiptId).collect(Collectors.toList())));
        // 单借据多付款的情况，找出实际报送的那个付款
        paymentBaseInfoList = reportDataRepository.filterNeedReportPaymentListSubTable(paymentBaseInfoList);
        Map<Long, PaymentBaseInfo> receiptPaymentMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getReceiptIdFinal, Function.identity(), (k1, k2) -> k1));
        // 合同主承租人map
        Map<Long, Boolean> contractReportMap = reportDataRepository.contractReportMap(needHandleDataList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()));

        // 逾期记录处理
        List<CrOverdueRecordDraft> reportDataList = new ArrayList<>();
        for (Long receiptId : overdueCollectionBaseInfoMap.keySet()) {
            List<CollectionBaseInfo> cbiList = overdueCollectionBaseInfoMap.get(receiptId);
            PaymentBaseInfo paymentBaseInfo = receiptPaymentMap.get(receiptId);
            // 付款申请为空=被过滤掉了不报，过滤掉没核销完毕的付款申请 和 主承租人不上报的合同数据 和 债权转让不报
            if (Objects.isNull(paymentBaseInfo) || !Boolean.TRUE.equals(contractReportMap.get(cbiList.get(0).getContractId()))) {
                continue;
            }
            // 最早应收日期
            LocalDate earlyestPlanCollectionDate = cbiList.stream().map(CollectionBaseInfo::getPlanCollectionDate).filter(Objects::nonNull).sorted(Comparator.naturalOrder()).findFirst().orElse(null);
            // 所有应收本金
            long allPrincipal = cbiList.stream().filter(c -> Objects.nonNull(c.getPrincipal())).mapToLong(CollectionBaseInfo::getPrincipal).sum();
            // 所有已收本金
            long allCollectionPrincipal = cbiList.stream().filter(c -> Objects.nonNull(c.getCollectionPrincipal())).mapToLong(CollectionBaseInfo::getCollectionPrincipal).sum();
            // 计算逾期总额
            long allOverdueAmount = 0L;
            //期项
            Set<String> phaseSet = new HashSet<>();
            for (CollectionBaseInfo c : cbiList) {
                allOverdueAmount += LongUtil.null2zero(c.getPrincipal());//本金
                allOverdueAmount += LongUtil.null2zero(c.getInterest());//利息
                allOverdueAmount += LongUtil.null2zero(c.getPenaltyInterest());//罚息
                allOverdueAmount -= LongUtil.null2zero(c.getCollectionPrincipal());//实收本金
                allOverdueAmount -= LongUtil.null2zero(c.getCollectionInterest());//实收利息
                allOverdueAmount -= LongUtil.null2zero(c.getCollectionPenaltyInterest());//实收罚息
                allOverdueAmount -= LongUtil.null2zero(c.getPenaltyInterestDeductionAmount());//罚息减免金额
                phaseSet.add(c.getPhase().toString());
            }

            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                    .eq(ContractBaseInfoLib::getOriginId, paymentBaseInfo.getContractId())
                    .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(ContractBaseInfoLib::getVersion)
                    .last(StringUtil.mysqlLimitOne())
            );

            CrOverdueRecordDraft crOverdueRecord = CrOverdueRecordDraft.builder().build();
            crOverdueRecord.setReportState(ReportState.TO_BE_REPORT.name())
                    .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                    .setPaymentId(paymentBaseInfo.getId())
                    .setPaymentApplyCode(ReportBizUtil.bizCalPaymentCode(paymentBaseInfo.getPaymentCode(), contractBaseInfoLib.getLeaseType()))
                    .setGenDate(dealTime.toLocalDate())
                    .setOverduePrincipal(Util.mithrasLongDecimalTwo(allPrincipal - allCollectionPrincipal))
                    .setOverdueTotal(Util.mithrasLongDecimalTwo(allOverdueAmount))
                    .setOverdueDay(Objects.nonNull(earlyestPlanCollectionDate) ? Math.toIntExact(dealTime.toLocalDate().toEpochDay() - earlyestPlanCollectionDate.toEpochDay()) : -1)
                    // 第一次逾期记录才需要变化日期 2023-03-27 -> 每一条都要
                    .setOverdueChangeDate(dealTime.toLocalDate().plusDays(-1))
                    .setPhase(phaseSet.toString());
            crOverdueRecord.setBusinessKey(crOverdueRecord.genBusinessKey(dealTime.toLocalDate()));
            crOverdueRecord.setContractId(cbiList.get(0).getContractId());
            reportDataList.add(crOverdueRecord);
        }
        // 从逾期变成不逾期处理
        Set<Long> overdueCancelPaymentIdSet = Sets.difference(lastDataPaymentIdMap.keySet(), paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet()));
        overdueCancelPaymentIdSet.forEach(p -> {
            CrOverdueRecordDraft crOverdueRecord = getNotOverdueRecordDraft(dealTime, lastDataPaymentIdMap, p);
            reportDataList.add(crOverdueRecord);
        });
        log.info("直租征信报送-逾期信息表处理，此次处理数量:{}, 记录逾期数据(未过滤条件):{}, 清除逾期记录:{}", reportDataList.size(),
                JSON.toJSONString(overdueCollectionBaseInfoMap.keySet()), JSON.toJSONString(overdueCancelPaymentIdSet));
        reportDataList.forEach(r -> {
            if (existDataMap.containsKey(r.getBusinessKey())) {
                CrOverdueRecordDraft existData = existDataMap.get(r.getBusinessKey());
                r.setId(existData.getId());
                // 变更日期设成一样的
                r.setOverdueChangeDate(existData.getOverdueChangeDate());
                if (ReportCompareUtil.checkChange(r, existData, existData.ignoreCompareFieldNames())) {
                    draftMapper.updateById(r);
                }
            }
        });
        reportDataList.removeIf(r -> Objects.nonNull(r.getId()));
        //同一天相同数据不能报多次
        List<CrOverdueRecordDraft> crOverdueRecordDrafts = crOverdueRecordDraftMapper.selectList(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .eq(CrOverdueRecordDraft::getOverdueChangeDate, dealTime.toLocalDate()));
        if (CollUtil.isEmpty(crOverdueRecordDrafts)) {
            crOverdueRecordDrafts = Collections.emptyList();
        }
        Map<String, List<CrOverdueRecordDraft>> listMap = crOverdueRecordDrafts.stream().collect(Collectors.groupingBy(CrOverdueRecordDraft::getPaymentApplyCode));
        List<CrOverdueRecordDraft> res = new LinkedList<>();
        if (CollUtil.isNotEmpty(reportDataList)) {
            for (CrOverdueRecordDraft draft : reportDataList) {
                List<CrOverdueRecordDraft> list = listMap.get(draft.getPaymentApplyCode());
                if (CollUtil.isEmpty(list)) {
                    res.add(draft);
                    reportDataRepository.deletePlanForOverdue(draft);
                    continue;
                }
                List<CrOverdueRecordDraft> collect = list.stream().filter(a -> a.getOverdueTotal().equals(draft.getOverdueTotal())).collect(Collectors.toList());
                if (CollUtil.isEmpty(collect)) {
                    draft.setId(listMap.get(draft.getPaymentApplyCode()).get(0).getId());
                    res.add(draft);
                    reportDataRepository.deletePlanForOverdue(draft);
                }
            }
            draftService.saveBatch(res);
        }
    }

    @NotNull
    private CrOverdueRecordDraft getNotOverdueRecordDraft(LocalDateTime dealTime, Map<Long, CrOverdueRecordDraft> lastDataPaymentIdMap, Long p) {
        CrOverdueRecordDraft crOverdueRecord = CrOverdueRecordDraft.builder().build();
        crOverdueRecord.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setPaymentId(p)
                .setPaymentApplyCode(lastDataPaymentIdMap.get(p).getPaymentApplyCode())
                .setOverduePrincipal(0L)
                .setOverdueTotal(0L)
                .setOverdueDay(0)
                .setGenDate(dealTime.toLocalDate())
                .setOverdueChangeDate(dealTime.toLocalDate().plusDays(-1))
                .setPhase(lastDataPaymentIdMap.get(p).getPhase());
        crOverdueRecord.setBusinessKey(crOverdueRecord.genBusinessKey(dealTime.toLocalDate()));
        crOverdueRecord.setContractId(lastDataPaymentIdMap.get(p).getContractId());
        return crOverdueRecord;
    }

    @Override
    public Integer sort() {
        return 28;
    }
}
