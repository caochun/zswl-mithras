package cn.zswltech.mithras.report.handler.impl.newimpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.base.model.CrAccountBase;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.draft.CrOverdueRecordDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrOverdueRecord;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrActualRepayDraftService;
import cn.zswltech.mithras.report.service.draft.CrOverdueRecordDraftService;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class CrOverdueNewHandler extends CrAbstractHandler<CrOverdueRecordDraft, CrOverdueRecord> {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private CrOverdueRecordDraftMapper crOverdueRecordDraftMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private CrActualRepayDraftService actualRepayDraftService;
    @Resource
    private CrRepayPlanDraftService repayPlanDraftService;
    @Resource
    private CrAccountDraftService accountDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.OVERDUE_RECORD_NEW;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        try {
            SpringContextHolder.getBean(CrOverdueNewHandler.class).handle(dealTime, lastDealTime);
        } catch (Exception e) {
            log.error("非直租逾期处理错误!", e);
        }
    }

    public void handle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        //将逾期处理成不逾期，只有全部还完了，才能处理成不逾期,但是多账户的情况下，可能一个付款申请下的某些账户是不逾期的
        LocalDateTime oldLastDealTime = lastDealTime;
        if (!lastDealTime.equals(LocalDateTime.MIN)) {
            oldLastDealTime = LocalDateTimeUtil.offset(lastDealTime, -1, ChronoUnit.DAYS);
        }
        //查询昨天的逾期记录
        List<CrOverdueRecordDraft> lastOverdueRecordDrafts = crOverdueRecordDraftMapper.selectList(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .ge(CrOverdueRecordDraft::getCreateTime, oldLastDealTime)
                .ne(CrOverdueRecordDraft::getOverdueTotal, 0)
                .ne(CrOverdueRecordDraft::getOverduePrincipal, 0)
                .ne(CrOverdueRecordDraft::getOverdueDay, 0)
                .lt(CrOverdueRecordDraft::getCreateTime, lastDealTime));
        //过滤直租
        if (CollUtil.isNotEmpty(lastOverdueRecordDrafts)) {
            lastOverdueRecordDrafts = lastOverdueRecordDrafts.stream()
                    .filter(a -> !a.getPaymentApplyCode().contains("HZ")).collect(Collectors.toList());
        }

        // 寻找此次需要处理的逾期
        List<CollectionBaseInfo> overdueCollectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .ne(CollectionBaseInfo::getPhase, 0)
                .le(CollectionBaseInfo::getPlanCollectionDate, dealTime.toLocalDate())
        );

        List<CrOverdueRecordDraft> needInsertData = new ArrayList<>();
        if (CollUtil.isNotEmpty(overdueCollectionBaseInfoList)) {
            //过滤掉直租的合同
            Set<Long> contractIds = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                            .in(ContractBaseInfoLib::getOriginId, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()))
                            .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL))
                    .stream()
                    .filter(a -> !LeaseType.zhi_zu.name().equals(a.getLeaseType()))
                    .map(ContractBaseInfoLib::getOriginId)
                    .collect(Collectors.toSet());

            //实际过滤
            overdueCollectionBaseInfoList = overdueCollectionBaseInfoList.stream()
                    .filter(a -> contractIds.contains(a.getContractId()))
                    .collect(Collectors.toList());

            //这里需要查询还款计划表，找出宽限期，然后做过滤
            if (CollUtil.isEmpty(overdueCollectionBaseInfoList)) {
                reportZero(lastOverdueRecordDrafts, dealTime);
                return;
            }
            Map<Long, Map<Integer, List<CrRepayPlanDraft>>> planMap = new HashMap<>(8);
            Map<Long, List<CrRepayPlanDraft>> contractPlanListMap = repayPlanDraftService.list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                            .in(CrRepayPlanDraft::getContractId, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()))
                            .in(CrRepayPlanDraft::getPhase, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getPhase).collect(Collectors.toList())))
                    .stream().collect(Collectors.groupingBy(CrRepayPlanDraft::getContractId));

            contractPlanListMap.forEach((contractId, planList) -> {
                planMap.put(contractId, planList.stream().collect(Collectors.groupingBy(CrRepayPlanDraft::getPhase)));
            });

            //中间变量，用户存储过滤后需要处理的数据
            List<CollectionBaseInfo> needHandleDataList = new ArrayList<>(overdueCollectionBaseInfoList.size());
            overdueCollectionBaseInfoList.sort(Comparator.comparing(CollectionBaseInfo::getPhase));
            for (CollectionBaseInfo collectionBaseInfo : overdueCollectionBaseInfoList) {
                //没有数据的话需要考虑宽限期, 但是前提是现在改合同在征信模块没有逾期信息
                Map<Integer, List<CrRepayPlanDraft>> integerListMap = planMap.get(collectionBaseInfo.getContractId());
                if (CollUtil.isEmpty(integerListMap)) {
                    continue;
                }
                List<CrRepayPlanDraft> crRepayPlanDrafts = integerListMap.get(collectionBaseInfo.getPhase());
                if (CollUtil.isEmpty(crRepayPlanDrafts)) {
                    log.error("合同ID为{}, 期项为{}的还款计划为空, 请业务验证数据的正确性！", collectionBaseInfo.getContractId(), collectionBaseInfo.getPhase());
                    continue;
                }
                for (CrRepayPlanDraft planDraft : crRepayPlanDrafts) {
                    String gracePeriod = reportDataRepository.getGracePeriod(planDraft);
                    LocalDate gracePeriodDate = dealTime.toLocalDate().minusDays(Long.parseLong(gracePeriod));
                    if (collectionBaseInfo.getPlanCollectionDate().isBefore(gracePeriodDate)) {
                        //还款时间比现在到过去宽限期的天数还要小，则认为是逾期， 并且多账户只需要添加一次
                        needHandleDataList.add(collectionBaseInfo);
                        break;
                    }
                }
            }

            if (CollUtil.isEmpty(needHandleDataList)) {
                //需要将昨天的数据报0
                reportZero(lastOverdueRecordDrafts, dealTime);
                return;
            }
            log.info("非直租需要处理的逾期收款数据contractIds：[{}]", needHandleDataList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()));
            //将逾期记录按照借据id分组
            Map<Long, List<CollectionBaseInfo>> receiptCodeCollectionMap = needHandleDataList.stream()
                    .collect(Collectors.groupingBy(CollectionBaseInfo::getReceiptId));

            //查询借据再查到付款ID
            Map<Long, PaymentBaseInfo> receiptIdPaymentBaseInfoMap = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .in(PaymentBaseInfo::getReceiptIdFinal, receiptCodeCollectionMap.keySet())
            ).stream().collect(Collectors.toMap(PaymentBaseInfo::getReceiptIdFinal, Function.identity()));

            List<CrRepayPlanDraft> interestList = new LinkedList<>();
            //将逾期的收款按照合同ID分组，并且找到最早逾期的日期作为逾期开始时间
            receiptCodeCollectionMap.forEach((receiptId, collectionBaseInfoList) -> {
                //由于数据库设计原因，这个关联可能为空，暂时先不处理
                PaymentBaseInfo paymentBaseInfo = receiptIdPaymentBaseInfoMap.get(receiptId);
                if (Objects.isNull(paymentBaseInfo)) {
                    return;
                }

                for (int i = 0; i < collectionBaseInfoList.size(); i++) {
                    CollectionBaseInfo collectionBaseInfo = collectionBaseInfoList.get(i);
                    //所有未还租金
                    long allNoPayRent = ifNull2Zero(collectionBaseInfo.getPlanCollectionAmount()) - ifNull2Zero(collectionBaseInfo.getCollectionAmount());
                    if (allNoPayRent <= 0) {
                        continue;
                    }
                    //根据付款申请ID和期项找出实际计划还款，判断是否逾期，这样查出来是当前合同的所有合同的还款计划
                    List<CrRepayPlanDraft> planDrafts = repayPlanDraftService.list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                            .eq(CrRepayPlanDraft::getContractId, collectionBaseInfo.getContractId())
                            .eq(CrRepayPlanDraft::getPaymentId, paymentBaseInfo.getId())
                            .eq(CrRepayPlanDraft::getPhase, collectionBaseInfo.getPhase()));

                    if (CollUtil.isEmpty(planDrafts)) {
                        return;
                    }

                    //将所有的计划罚息累加，后面计算增量罚息的时候使用
                    Long allInterest = Optional.of(planDrafts.stream()
                            .filter(a -> Objects.nonNull(a.getPlanPenaltyInterest()))
                            .mapToLong(a -> ifNull2Zero(a.getPlanPenaltyInterest())).sum()).orElse(0L);

                    //查询当前期项实际的还款记录, 按照编号分组
                    Map<String, List<CrActualRepayDraft>> codeListMap = actualRepayDraftService.list(Wrappers.<CrActualRepayDraft>lambdaQuery()
                            .eq(CrActualRepayDraft::getPhase, collectionBaseInfo.getPhase())
                            .eq(CrActualRepayDraft::getPaymentId, paymentBaseInfo.getId())
                            .eq(CrActualRepayDraft::getContractId, paymentBaseInfo.getContractId())
                    ).stream().collect(Collectors.groupingBy(CrActualRepayDraft::getPaymentApplyCode));

                    //总的罚息，第一次逾期的时候直接全部计算出来分摊到各自的账户上，后续产生的罚息需要按照未还的金额进行分摊
                    long rentPenaltyInterest = ifNull2Zero(collectionBaseInfo.getPenaltyInterest()) - ifNull2Zero(collectionBaseInfo.getCollectionPenaltyInterest());
                    //新增罚息
                    long addInterest = rentPenaltyInterest - allInterest;
                    for (int j = 0; j < planDrafts.size(); j++) {
                        CrRepayPlanDraft currentPlan = planDrafts.get(j);
                        List<CrActualRepayDraft> actualRepayDrafts = codeListMap.get(currentPlan.getPaymentApplyCode());
                        //如果这个还款期项对应的账户未还款
                        if (CollUtil.isEmpty(actualRepayDrafts)) {
                            BigDecimal allRent = BigDecimal.valueOf(collectionBaseInfo.getPlanCollectionAmount() - ifNull2Zero(collectionBaseInfo.getCollectionAmount()));
                            if (allRent.equals(BigDecimal.ZERO)) {
                                continue;
                            }
                            BigDecimal interest = BigDecimal.valueOf(currentPlan.getRent())
                                    .divide(allRent, 20, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(addInterest));
                            needInsertData.add(getNoRepayAccount(dealTime, collectionBaseInfo, currentPlan,
                                    interest.add(BigDecimal.valueOf(currentPlan.getPlanPenaltyInterest() - currentPlan.getAlreadyPenaltyInterest()))));
                            //罚息要记录下来，但是这里没有还钱，重新再分摊罚息即可
                            currentPlan.setPlanPenaltyInterest(Util.mithrasLongDecimalTwo(Math.max(0, interest.longValue()) + ifNull2Zero(currentPlan.getPlanPenaltyInterest())));
                            interestList.add(BeanUtil.copyProperties(currentPlan, CrRepayPlanDraft.class));
                            continue;
                        }

                        //算出总的收款以及本金
                        long totalAmount = actualRepayDrafts.stream().mapToLong(CrActualRepayDraft::getCollectionAmount).sum();
                        long totalPrincipal = actualRepayDrafts.stream().mapToLong(CrActualRepayDraft::getCollectionPrincipal).sum();
                        if (currentPlan.getRent() == totalAmount) {
                            //这一期已还完
                            continue;
                        }

                        //这一期只还了一半,这里还需要考虑这一期还了这一半之后还清了需要修改逾期时间, 然后还需要增加新增的罚息，按照未还租金进行按比例计算
                        if (currentPlan.getRent() > totalAmount) {
                            CrOverdueRecordDraft draft = CrOverdueRecordDraft.builder().build();
                            draft.setReportState(ReportState.TO_BE_REPORT.name())
                                    .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                                    .setProcBusinessKey(null)
                                    .setPaymentId(currentPlan.getPaymentId())
                                    .setPaymentApplyCode(currentPlan.getPaymentApplyCode())
                                    .setGenDate(dealTime.toLocalDate())
                                    .setOverduePrincipal(Util.mithrasLongDecimalTwo(currentPlan.getPrincipal() - totalPrincipal))
                                    .setOverdueDay(Objects.nonNull(currentPlan.getCashFlowDate()) ? Math.toIntExact(dealTime.toLocalDate().toEpochDay() - currentPlan.getCashFlowDate().toEpochDay()) : -1)
                                    // 第一次逾期记录才需要变化日期
                                    .setOverdueChangeDate(dealTime.toLocalDate().plusDays(-1))
                                    .setPhase(collectionBaseInfo.getPhase().toString());
                            //计算罚息，先算出之前的未还的罚息，再加上新增的罚息
                            long noPayInterest = ifNull2Zero(currentPlan.getPlanPenaltyInterest()) - ifNull2Zero(currentPlan.getAlreadyPenaltyInterest());
                            //新增罚息, 计算公式：当前还款计划未还租金 / 当前期项所有未还租金 * 新增的罚息
                            BigDecimal currentAddInterest = BigDecimal.valueOf(addInterest * ifNull2Zero(currentPlan.getRent() - totalAmount))
                                    .divide(BigDecimal.valueOf(allNoPayRent), 20, RoundingMode.HALF_UP);
                            draft.setOverdueTotal(Util.mithrasLongDecimalTwo(currentPlan.getRent() - totalAmount + noPayInterest + currentAddInterest.longValue()));
                            draft.setBusinessKey(draft.genBusinessKey(dealTime.toLocalDate()));
                            draft.setContractId(collectionBaseInfo.getContractId());
                            currentPlan.setPlanPenaltyInterest(Util.mithrasLongDecimalTwo(ifNull2Zero(currentPlan.getPlanPenaltyInterest()) + ifNull2Zero(currentAddInterest.longValue())));
                            interestList.add(BeanUtil.copyProperties(currentPlan, CrRepayPlanDraft.class));
                            needInsertData.add(draft);
                        }
                    }
                }

            });

            //保存新增的罚息
            if (CollUtil.isNotEmpty(interestList)) {
                SpringContextHolder.getBean(CrRepayPlanDraftService.class).updateBatchById(interestList);
            }
        }

        //这里需要优化一下，如果没有新的逾期，但是昨天存在逾期的情况，需要将两个部分分开处理
        List<CrOverdueRecordDraft> tempList = new ArrayList<>();
        List<CrOverdueRecordDraft> res = new LinkedList<>();
        if (CollUtil.isNotEmpty(needInsertData)) {
            Map<String, List<CrOverdueRecordDraft>> listMap = needInsertData.stream().collect(Collectors.groupingBy(CrOverdueRecordDraft::getPaymentApplyCode));
            listMap.forEach((k, v) -> {
                v.sort(Comparator.comparing(CrOverdueRecordDraft::getOverdueDay).reversed());
                CrOverdueRecordDraft recordDraft = v.get(0);
                //创建一个set数组容纳期项
                Set<String> phaseSet = new HashSet<>();
                for(int i = 0; i < v.size(); i++) {
                    phaseSet.add(v.get(i).getPhase());
                }
                CrOverdueRecordDraft draft = CrOverdueRecordDraft.builder().build();
                draft.setReportState(ReportState.TO_BE_REPORT.name())
                        .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                        .setPaymentId(recordDraft.getPaymentId())
                        .setPaymentApplyCode(recordDraft.getPaymentApplyCode())
                        .setGenDate(dealTime.toLocalDate())
                        .setOverduePrincipal(Util.mithrasLongDecimalTwo(v.stream().mapToLong(CrOverdueRecordDraft::getOverduePrincipal).sum()))
                        .setOverdueDay(recordDraft.getOverdueDay())
                        // 第一次逾期记录才需要变化日期
                        .setOverdueChangeDate(recordDraft.getOverdueChangeDate())
                        .setPhase(phaseSet.toString());
                //罚息
                draft.setOverdueTotal(Util.mithrasLongDecimalTwo(v.stream().mapToLong(CrOverdueRecordDraft::getOverdueTotal).sum()));
                draft.setBusinessKey(draft.genBusinessKey(dealTime.toLocalDate()));
                draft.setContractId(v.get(0).getContractId());
                tempList.add(draft);
            });

            //同一天相同数据不能报多次
            List<CrOverdueRecordDraft> crOverdueRecordDrafts = Optional.ofNullable(crOverdueRecordDraftMapper.selectList(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                    .eq(CrOverdueRecordDraft::getOverdueChangeDate, dealTime.toLocalDate()))).orElse(Collections.emptyList());

            Map<String, CrOverdueRecordDraft> map = crOverdueRecordDrafts.stream().collect(Collectors.toMap(CrOverdueRecordDraft::getPaymentApplyCode, Function.identity()));
            if (CollUtil.isNotEmpty(tempList)) {
                for (CrOverdueRecordDraft draft : tempList) {
                    CrOverdueRecordDraft recordDraft = map.get(draft.getPaymentApplyCode());
                    if (Objects.isNull(recordDraft)) {
                        res.add(draft);
                        continue;
                    }
                    draft.setId(map.get(draft.getPaymentApplyCode()).getId());
                    draft.setOverdueTotal(draft.getOverdueTotal());
                    draft.setOverduePrincipal(draft.getOverduePrincipal());
                    res.add(draft);
                }
            }
        }

        if (CollUtil.isNotEmpty(lastOverdueRecordDrafts)) {
            //在插入当前逾期记录的时候，筛选出昨天存在的账户，今天不存在的账户，需要变成不逾期
            List<String> existAccount = lastOverdueRecordDrafts.stream()
                    .filter(a -> a.getOverdueTotal() > 0 || a.getOverduePrincipal() > 0)
                    .map(CrOverdueRecordDraft::getPaymentApplyCode)
                    .distinct()
                    .collect(Collectors.toList());

            //把昨天的和今天的进行对比，过滤出需要修改的数据
            List<String> newAccount = tempList.stream().map(CrOverdueRecordDraft::getPaymentApplyCode).distinct().collect(Collectors.toList());

            List<String> needChangeList = existAccount.stream().filter(a -> !newAccount.contains(a)).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(needChangeList)) {
                List<CrOverdueRecordDraft> draftList = lastOverdueRecordDrafts.stream().filter(a -> needChangeList.contains(a.getPaymentApplyCode())).collect(Collectors.toList());
                for (CrOverdueRecordDraft last : draftList) {
                    CrOverdueRecordDraft crOverdueRecord = CrOverdueRecordDraft.builder().build();
                    crOverdueRecord.setReportState(ReportState.TO_BE_REPORT.name())
                            .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                            .setPaymentId(last.getPaymentId())
                            .setPaymentApplyCode(last.getPaymentApplyCode())
                            .setOverduePrincipal(0L)
                            .setOverdueTotal(0L)
                            .setOverdueDay(0)
                            .setGenDate(dealTime.toLocalDate())
                            .setOverdueChangeDate(dealTime.toLocalDate().plusDays(-1));
                    crOverdueRecord.setBusinessKey(crOverdueRecord.genBusinessKey(dealTime.toLocalDate()));
                    crOverdueRecord.setContractId(last.getContractId());
                    crOverdueRecord.setPhase(last.getPhase());
                    res.add(crOverdueRecord);
                }
            }

        }

        if (CollUtil.isNotEmpty(res)) {
            for(CrOverdueRecordDraft recordDraft : res){
                reportDataRepository.deletePlanForOverdue(recordDraft);
            }
            //逾期的日期以第一次逾期的时间为准
            SpringContextHolder.getBean(CrOverdueRecordDraftService.class).saveOrUpdateBatch(res);
        }
    }

    private void reportZero(List<CrOverdueRecordDraft> lastOverdueRecordDrafts, LocalDateTime dealTime) {
        //需要将昨天的数据报0
        if (CollUtil.isNotEmpty(lastOverdueRecordDrafts)) {
            List<CrOverdueRecordDraft> needUpdateList = new ArrayList<>(lastOverdueRecordDrafts.size());
            for (CrOverdueRecordDraft draft : lastOverdueRecordDrafts) {
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
    }

    @NotNull
    private CrOverdueRecordDraft getNoRepayAccount(LocalDateTime dealTime, CollectionBaseInfo collectionBaseInfo,
                                                   CrRepayPlanDraft currentPlan, BigDecimal interest) {
        CrOverdueRecordDraft draft = new CrOverdueRecordDraft();
        draft.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setProcBusinessKey(null)
                .setPaymentId(currentPlan.getPaymentId())
                .setPaymentApplyCode(currentPlan.getPaymentApplyCode())
                .setGenDate(dealTime.toLocalDate())
                .setOverduePrincipal(Util.mithrasLongDecimalTwo(ifNull2Zero(currentPlan.getPrincipal())))
                .setOverdueDay(Objects.nonNull(currentPlan.getCashFlowDate()) ? Math.toIntExact(dealTime.toLocalDate().toEpochDay() - currentPlan.getCashFlowDate().toEpochDay()) : -1)
                // 第一次逾期记录才需要变化日期
                .setOverdueChangeDate(dealTime.toLocalDate().plusDays(-1))
                .setPhase(collectionBaseInfo.getPhase().toString());
        //按照账户金额计算各自所占的罚息
        draft.setOverdueTotal(Util.mithrasLongDecimalTwo(interest.longValue() + currentPlan.getRent()));
        draft.setBusinessKey(draft.genBusinessKey(dealTime.toLocalDate()));
        draft.setContractId(collectionBaseInfo.getContractId());
        return draft;
    }

    private Long ifNull2Zero(Long number) {
        if (Objects.isNull(number)) {
            return 0L;
        }
        return number;
    }

    @Override
    public Integer sort() {
        return 69;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void afterModuleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        //查询这段时间内已经有结清日期的账户
        List<CrAccountDraft> closeAccount = accountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrAccountDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                .ge(CrAccountDraft::getUpdateTime, lastDealTime)
                .isNotNull(CrAccountDraft::getClosedDate)
        );

        if (CollUtil.isEmpty(closeAccount)) {
            return;
        }

        Map<Long, List<CrAccountDraft>> listMap = closeAccount.stream().collect(Collectors.groupingBy(CrAccountDraft::getContractId));
        //将这些账户的实际收款信息集中
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, closeAccount.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet()))
                .in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(), CollectionWriteOffStatusEnum.UNCOLLECTION.name())));

        if (CollUtil.isEmpty(collectionBaseInfos)) {
            return;
        }

        Map<Long, List<CollectionBaseInfo>> contractIdDoMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        //计算出总的罚息
        List<CrAccountDraft> res = new ArrayList<>();
        contractIdDoMap.forEach((contractId, collectionBaseInfoList) -> {
            long totalInterest = collectionBaseInfoList.stream().mapToLong(a -> ifNull2Zero(a.getPenaltyInterest()) - ifNull2Zero(a.getCollectionPenaltyInterest())).sum();

            if (totalInterest != 0) {
                res.addAll(Optional.ofNullable(listMap.get(contractId)).orElse(Collections.emptyList()));
            }
        });

        if (CollUtil.isNotEmpty(res)) {
            accountDraftService.lambdaUpdate()
                    .set(CrAccountBase::getClosedDate, null)
                    .in(CrBaseModel::getContractId, res.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet()))
                    .update();
            log.info("非直租不应该结清且已经结清的账户：{}", res.stream().map(CrAccountDraft::getId).collect(Collectors.toList()));
        }
    }
}
