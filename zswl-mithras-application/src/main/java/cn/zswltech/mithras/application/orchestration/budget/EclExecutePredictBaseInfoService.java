package cn.zswltech.mithras.application.orchestration.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.application.EclExecuteClientPromotionResultService;
import cn.zswltech.mithras.budget.application.EclPredictBusinessConfigService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.budget.application.EclExecutePredictBaseInfoApplicationService;
import cn.zswltech.mithras.budget.application.EclExecutePredictRecordApplicationService;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.fuxi.common.dto.RatingManagementRSP;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoAddREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoListREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoListRSP;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoRemoveREQ;
import cn.zswltech.mithras.dto.kpi.KpiExpectedLossDecisionQuery;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteEclResult;
import cn.zswltech.mithras.rating.feign.RatingManagementClient;
import cn.zswltech.mithras.rating.versioning.ratingclient.RatingClientLibService;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import cn.zswltech.mithras.rating.service.DecisionService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.kpi.bo.*;
import cn.zswltech.mithras.kpi.enums.EclOuterLevelEnum;
import cn.zswltech.mithras.kpi.enums.KpiRatingModelGroupEnum;
import cn.zswltech.mithras.kpi.enums.config.EclConfigEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.budget.mapper.EclExecutePredictBaseInfoMapper;
import cn.zswltech.mithras.kpi.mapper.EclExecuteRecordMapper;
import cn.zswltech.mithras.budget.mapper.model.*;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.kpi.model.EclBusinessConfig;
import cn.zswltech.mithras.kpi.model.EclExecuteRecord;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.foundation.bo.*;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.kpi.application.ecl.EclBusinessConfigService;
import cn.zswltech.mithras.application.orchestration.kpi.EclExecuteRecordService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @description 资产减值预测表
* @author vico
* @date 2025-10-14
*/
@Service
@Slf4j
public class EclExecutePredictBaseInfoService extends ServiceImpl<EclExecutePredictBaseInfoMapper, EclExecutePredictBaseInfo> implements EclExecutePredictBaseInfoApplicationService {


    @Resource
    private EclBusinessConfigService eclBusinessConfigService;
    @Resource
    private EclExecuteRecordService eclExecuteRecordService;
    @Resource
    private EclExecuteRecordMapper eclExecuteRecordMapper;
    @Resource
    private EclPredictBusinessConfigService eclPredictBusinessConfigService;
    @Resource
    private EclExecutePredictRecordApplicationService eclExecutePredictRecordService;
    @Resource
    private BudgetPlanProfitDetailService budgetPlanProfitDetailService;
    @Resource
    private BudgetPlanService budgetPlanService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;
    @Resource
    private DecisionService decisionService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibHandler projReviewBaseInfoLibHandler;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private RatingManagementClient ratingManagementClient;

    private final static String BASE_SCENE = "基准情景";
    private final static String OPT_SCENE = "乐观情景";
    private final static String GLO_SCENE = "悲观情景";
    private final static String ECL_PD_TEMP = "ecl_pd_temp";
    private final static String ECL_OUTER_LEVEL = "ecl_outer_level";
    private final static String ECL_EAD = "ecl_ead";
    private final static String ECL_STEP = "ecl_step";
    private final static String ECL_FACTOR_T = "ecl_factor_t";
    private final static String BASE_PD_FORWARD = "base_pd_forward";
    private final static String OPT_PD_FORWARD = "opt_pd_forward";
    private final static String GLO_PD_FORWARD = "glo_pd_forward";
    private final static String ECL_BASE_IFRS9 = "ecl_base_ifrs9";
    private final static String ECL_OPT_IFRS9 = "ecl_opt_ifrs9";
    private final static String ECL_GLO_IFRS9 = "ecl_glo_ifrs9";
    private final static String BASE_ECL = "base_ecl";
    private final static String OPT_ECL = "opt_ecl";
    private final static String GLO_ECL = "glo_ecl";
    private final static String ECL = "ecl";
    private final static String ECL_STEP_PROMOTION= "ecl_step_promotion";
    private final static String ONE_HUNDRED_MILLION = "100000000";
    private final static List<String> CCCList = ListUtil.toList("C", "CC", "CCC");



    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.NESTED)
    public Long create(EclExecutePredictBaseInfoAddREQ req) {
        EclExecutePredictBaseInfo info = BeanUtil.copyProperties(req, EclExecutePredictBaseInfo.class);
        if (ObjectUtil.isNotEmpty(req.getBudgetPlanId())) {
            BudgetPlan budgetPlan = budgetPlanService.getById(req.getBudgetPlanId());
            if (ObjectUtil.isEmpty(budgetPlan)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            info.setPredictDataBegan(budgetPlan.getBudgetDateFrom().minusDays(1));
            info.setPredictDataEnd(budgetPlan.getBudgetDateTo());
        } else if (ObjectUtil.isNotEmpty(req.getPredictDataFrom())){
            info.setPredictDataBegan(req.getPredictDataFrom().with(TemporalAdjusters.lastDayOfMonth()));
            info.setPredictDataEnd(req.getPredictDataTo());
        } else {
            return null;
        }
        this.save(info);
        LocalDate localDateNow = LocalDate.now();
        //同步参数
        List<EclBusinessConfig> businessConfigs = eclBusinessConfigService.list();
        List<EclPredictBusinessConfig> eclPredictBusinessConfigs = BeanUtil.copyToList(businessConfigs, EclPredictBusinessConfig.class);
        eclPredictBusinessConfigs.forEach(e -> {
            e.setExecutePredictId(info.getId());
        });
        eclPredictBusinessConfigService.saveBatch(eclPredictBusinessConfigs);
        //同步减值相关数据
        List<String> contractCodes = new ArrayList<>();
        Map<String, Long> contractCode2BudgetPlanProfitDetail = new HashMap<>();
        List<BudgetPlanProfitDetail> budgetPlanProfitDetails = null;
        if (ObjectUtil.isNotEmpty(req.getBudgetPlanId())) {
            //自动创建
            budgetPlanProfitDetails = budgetPlanProfitDetailService.list(Wrappers.<BudgetPlanProfitDetail>lambdaQuery()
                    .eq(BudgetPlanProfitDetail::getBudgetPlanId, req.getBudgetPlanId())
                    .gt(BudgetPlanProfitDetail::getReceiptId, 0)
                    .eq(BudgetPlanProfitDetail::getDataCategory, BudgetPlanDataCategoryEnum.HISTORY.name()));
            contractCodes.addAll(budgetPlanProfitDetails.stream().map(BudgetPlanProfitDetail::getContractCode).collect(Collectors.toList()));
            contractCode2BudgetPlanProfitDetail.putAll(budgetPlanProfitDetails.stream().collect(Collectors.toMap(BudgetPlanProfitDetail::getContractCode, BudgetPlanProfitDetail::getId, (a, b) -> b)));
        }
        //过滤结清合同
        Set<String> settleContract = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name()))
                .stream().map(ContractBaseInfo::getContractCode).collect(Collectors.toSet());

        List<EclExecuteRecord> eclExecuteRecords = eclExecuteRecordMapper.getLastList(contractCodes);
        if (ObjectUtil.isNotEmpty(eclExecuteRecords)) {
            eclExecuteRecords = eclExecuteRecords.stream().filter(e -> !settleContract.contains(e.getContractCode())).collect(Collectors.toList());
        }
        if(ObjectUtil.isNotEmpty(eclExecuteRecords)) {
            //获取已经有的数据
            List<EclExecutePredictRecord> eclExecutePredictRecords = BeanUtil.copyToList(eclExecuteRecords, EclExecutePredictRecord.class);
            eclExecutePredictRecords.forEach(e -> {
                e.setExecutePredictId(info.getId());
                e.setBudgetPlanProfitDetailId(contractCode2BudgetPlanProfitDetail.get(e.getContractCode()));
                e.setEad(null);
                e.setEclStep(null);
                e.setEclParamZ(null);
                e.setEclParamWeight(null);
                e.setBasePdForward(null);
                e.setOptPdForward(null);
                e.setGloPdForward(null);
                e.setEclBaseIfrs9(null);
                e.setEclOptIfrs9(null);
                e.setEclGloIfrs9(null);
                e.setBaseEcl(null);
                e.setOptEcl(null);
                e.setGloEcl(null);
                e.setEcl(null);
                e.setPromotionResultHandle(null);
                if (ObjectUtil.isNotEmpty(e.getLateDate()) && e.getLateDate().isAfter(localDateNow)) {
                    e.setLateDay(0);
                    e.setLateDate(null);
                }
            });
            //
            //分为两个月份，开始+结束区间月份。如果逾期需计算逾期情况数据
            //填充最早逾期天数
           /* Map<String, LocalDate> maxOverdueDay = getMaxOverdueDay(contractCodes);
            eclExecutePredictRecords.forEach(e -> {
                e.setLateDate(maxOverdueDay.get(e.getContractCode()));
            });*/
            List<Long> contractIds = eclExecutePredictRecords.stream().filter(e -> ObjectUtil.isNotEmpty(e.getContractId())).map(EclExecutePredictRecord::getContractId).collect(Collectors.toList());
            //查询合同基本信息
            List<ContractBaseInfo> contractBaseInfos;
            //获取合同类型
            Map<Long, String> contractId2BeanMap = new HashMap<>();
            if (ObjectUtil.isNotEmpty(contractIds)) {
                contractBaseInfos = contractBaseInfoService.listByIds(contractIds);
                if (CollectionUtils.isNotEmpty(contractBaseInfos)) {
                    contractId2BeanMap.putAll(contractBaseInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getLeaseType())).collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getLeaseType, (a, b) -> b)));
                }
            }

            //获取逾期的数据
            List<EclExecutePredictRecord> overdueRecordList = eclExecutePredictRecords.stream().filter(e -> ObjectUtil.isNotEmpty(e.getLateDay())).filter(e -> e.getLateDay() > 0).collect(Collectors.toList());
            List<EclExecutePredictRecord> addRecords = new ArrayList<>();
            //计算非逾期状态下
            addRecords.addAll(getExecutePredictRecordByMonth(eclExecutePredictRecords, info.getPredictDataBegan(), false, contractId2BeanMap));
            addRecords.addAll(getExecutePredictRecordByMonth(overdueRecordList, info.getPredictDataBegan(), true, contractId2BeanMap));
            if(ObjectUtil.isNotEmpty(info.getPredictDataEnd()) && info.getPredictDataEnd().isAfter(info.getPredictDataBegan())) {
                addRecords.addAll(getExecutePredictRecordByMonth(eclExecutePredictRecords, info.getPredictDataEnd(), false, contractId2BeanMap));
                addRecords.addAll(getExecutePredictRecordByMonth(overdueRecordList, info.getPredictDataEnd(), true, contractId2BeanMap));
            }
            if (ObjectUtil.isNotEmpty(budgetPlanProfitDetails)) {
                Map<String, Long> receiptId2BudgetPlanProfitDetailId = budgetPlanProfitDetails.stream().collect(Collectors.toMap(BudgetPlanProfitDetail::getReceiptCode, BudgetPlanProfitDetail::getId, (a, b) -> b));
                addRecords.forEach(e -> {
                    e.setBudgetPlanProfitDetailId(receiptId2BudgetPlanProfitDetailId.get(e.getReceiptCode()));
                });
            }
            eclExecutePredictRecordService.saveRecords(addRecords);
        }
        //调用计算方法
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                CompletableFuture.supplyAsync(() -> {
                    try {
                        calculation(info.getId(), null);
                    } catch (Exception e) {
                        throw new MithrasException("测算失败");
                    }
                    return null;
                });
            }});

        return info.getId();
    }

    //计算不同月份下
    private List<EclExecutePredictRecord> getExecutePredictRecordByMonth(List<EclExecutePredictRecord> eclExecutePredictRecords, LocalDate month, boolean isOverdue, Map<Long, String> contractId2BeanMap) {
        if (CollectionUtil.isEmpty(eclExecutePredictRecords)) {
            return new ArrayList<>();
        }
        Set<Long> receiptIds = eclExecutePredictRecords.stream().map(EclExecutePredictRecord::getReceiptId).collect(Collectors.toSet());
        List<EclExecutePredictRecord> newEclExecutePredictRecords = BeanUtil.copyToList(eclExecutePredictRecords, EclExecutePredictRecord.class);

        LocalDate now = LocalDate.now();
        if (isOverdue) {
            //获取客户下实际剩余本金
            List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getReceiptId, receiptIds)
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .gt(CollectionBaseInfo::getPhase, 0));
            Map<Long, Long> receiptId2RemainPrincipal = list.stream().filter(e -> ObjectUtil.isNotEmpty(e.getReceiptId())).collect(Collectors.toMap(CollectionBaseInfo::getReceiptId, e -> LongUtil.null2zero(e.getPrincipal()) - LongUtil.null2zero(e.getCollectionPrincipal()),
                    (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
            newEclExecutePredictRecords.forEach(e -> {
                e.setOverdueFlag(YesOrNoNumberEnum.YES.getCode());
                e.setRemainPrincipal(LongUtil.tenThousand2Dollar(LongUtil.null2zero(receiptId2RemainPrincipal.get(e.getReceiptId()))));
                //逾期的认为是0
                e.setAccruedInterest(BigDecimal.ZERO);
                e.setLateDay((int) Math.max(ChronoUnit.DAYS.between(e.getLateDate() == null ?  now : e.getLateDate(), month), 0));
            });
        } else {
            //剩余本金
            List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getReceiptId, receiptIds)
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .gt(CollectionBaseInfo::getPhase, 0)
                    .gt(CollectionBaseInfo::getPlanCollectionDate, month));
            Map<Long, Long> receiptId2RemainPrincipal = list.stream().filter(e -> ObjectUtil.isNotEmpty(e.getReceiptId())).collect(Collectors.toMap(CollectionBaseInfo::getReceiptId, e -> LongUtil.null2zero(e.getPrincipal()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
            newEclExecutePredictRecords.forEach(e -> {
                e.setRemainPrincipal(LongUtil.tenThousand2Dollar(receiptId2RemainPrincipal.get(e.getReceiptId())));
                e.setOverdueFlag(YesOrNoNumberEnum.NO.getCode());
                e.setLateDay(0);
            });
        }
        //应计利息
        Map<Long, Long> accruedInterestMap = contractIncomeSharingService.getAccruedInterest(newEclExecutePredictRecords.stream().map(EclExecutePredictRecord::getReceiptId).collect(Collectors.toList()), month);
        //下期租金
        Map<Long, Long> receiptNextRentMap = SpringContextHolder.getBean(CollectionBaseInfoService.class).getReceiptNextRent(receiptIds, month);

        newEclExecutePredictRecords.forEach(e -> {
            e.setId(null);
            e.setCalculationDate(month);
            //应计利息（只有业务类型不等于经营性租赁才有值）、下期租金（只有业务类型等于经营性租赁才有值）
            if (LeaseType.jyx_zu.name().equals(contractId2BeanMap.get(e.getContractId()))) {
                e.setNextRent(LongUtil.tenThousand2Dollar(receiptNextRentMap.get(e.getReceiptId())));
            }
            if (!isOverdue){
                e.setAccruedInterest(LongUtil.tenThousand2Dollar(Math.max(0, LongUtil.null2zero(accruedInterestMap.get(e.getReceiptId())))));
            }
            if(ObjectUtil.isEmpty(e.getRemainPrincipal()) || e.getRemainPrincipal().compareTo(BigDecimal.ZERO) <= 0) {
                e.setAccruedInterest(BigDecimal.ZERO);
            }
            if(ObjectUtil.isNotEmpty(e.getContractExpirationDate()) && month.isAfter(e.getContractExpirationDate()) && !isOverdue) {
                e.setDeposit(BigDecimal.ZERO);
            }
            if (ObjectUtil.isNotEmpty(e.getRemainPrincipal())) {
                e.setRiskExposure(e.getRemainPrincipal().subtract((e.getDeposit() == null ? BigDecimal.ZERO : e.getDeposit())));
            }
        });
        return newEclExecutePredictRecords;
    }

    private Map<String, LocalDate> getMaxOverdueDay(List<String> contractCodes) {
        if(ObjectUtil.isEmpty(contractCodes)) {
            return MapUtil.empty();
        }
        LocalDate now = LocalDate.now();
        // 逾期天数
        Map<String, LocalDate> overdueMaxDateTime = new HashMap<>();
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractCode, contractCodes)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .lt(CollectionBaseInfo::getPlanCollectionDate, now));
        Map<String, List<CollectionBaseInfo>> collectionMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractCode));
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            overdueMaxDateTime = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<CollectionBaseInfo> collectionBaseInfos = collectionMap.get(entry.getKey());
                return collectionBaseInfos.stream().filter(f -> f.getPlanCollectionDate().isBefore(now)).filter(e -> {
                    long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
                    long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                    return planRent > actualRent;
                }).map(CollectionBaseInfo::getPlanCollectionDate).min(Comparator.comparing(Function.identity())).orElse(now);
            }));
        }
        return overdueMaxDateTime;
    }

    public Page<EclExecutePredictBaseInfo> list(EclExecutePredictBaseInfoListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<EclExecutePredictBaseInfo>lambdaQuery());
    }

    public Long createFromManual(EclExecutePredictBaseInfoAddREQ req) {
        req.setSource(YesOrNoNumberEnum.YES.getCode());
        return this.create(req);
    }

    @Override
    public EclExecutePredictBaseInfo getById(Long id) {
        return super.getById(id);
    }

    public PageR<EclExecutePredictBaseInfoListRSP> pageList(EclExecutePredictBaseInfoListREQ req) {
        Page<EclExecutePredictBaseInfo> data = this.list(req);
        List<EclExecutePredictBaseInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), EclExecutePredictBaseInfoListRSP.class);
        return PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize());
    }

    public void calculationAsync(Long id) {
        CompletableFuture.supplyAsync(() -> {
            try {
                this.calculation(id, null);
            } catch (Exception e) {
                throw new MithrasException("测算失败");
            }
            return null;
        });
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(EclExecutePredictBaseInfoRemoveREQ req) {
        EclExecutePredictBaseInfo originalInfo = this.getById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaUpdateWrapper<EclExecutePredictBaseInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(EclExecutePredictBaseInfo::getDeleted, YesOrNoNumberEnum.YES.getCode());
        lambdaUpdateWrapper.eq(EclExecutePredictBaseInfo::getId, req.getId());
        this.update(lambdaUpdateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void removeByBudgetPlan(Long budgetPlanId) {
        EclExecutePredictBaseInfo originalInfo = this.getOne(Wrappers.<EclExecutePredictBaseInfo>lambdaQuery()
        .eq(EclExecutePredictBaseInfo::getBudgetPlanId, budgetPlanId));
        if (ObjectUtil.isNull(originalInfo)) {
           return;
        }
        LambdaUpdateWrapper<EclExecutePredictBaseInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(EclExecutePredictBaseInfo::getDeleted, YesOrNoNumberEnum.YES.getCode());
        lambdaUpdateWrapper.eq(EclExecutePredictBaseInfo::getBudgetPlanId, budgetPlanId);
        this.update(lambdaUpdateWrapper);
        //删除子数据
        eclExecutePredictRecordService.removeByExecutePredictId(originalInfo.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void calculation(Long eclExecutePredictBaseInfoId, List<Long> executePredictRecordList) {
        log.info("EclExecutePredictBaseInfoService calculation param eclExecutePredictBaseInfoId = {}, executePredictRecordList = {}", eclExecutePredictBaseInfoId, executePredictRecordList);
        EclExecutePredictBaseInfo executePredictBaseInfo = this.getById(eclExecutePredictBaseInfoId);
        if (ObjectUtil.isEmpty(executePredictBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<BudgetEclRiskReserveBO> riskReserveBOS = new ArrayList<>();
        boolean isSuccess = true;
        try {
            //获取详情数据
            List<EclExecutePredictRecord> executePredictRecords = eclExecutePredictRecordService.listByExecutePredictId(eclExecutePredictBaseInfoId, executePredictRecordList);
            //转模型入参
            if (ObjectUtil.isEmpty(executePredictRecords)) {
                return;
            }
            LocalDate localDateNow = LocalDate.now();
            //刷新逾期天数
            executePredictRecords.stream().filter(e-> ObjectUtil.equals(e.getOverdueFlag(), YesOrNoNumberEnum.YES.getCode())).forEach(e -> {
                if (ObjectUtil.isEmpty(e.getLateDate())) {
                    e.setLateDay(0);
                } else {
                    e.setLateDay((int) Math.max(ChronoUnit.DAYS.between(e.getLateDate(), e.getCalculationDate() == null ? localDateNow : e.getCalculationDate()), 0));
                }
            });
            //调用模型计算数据
            calculationByModel(executePredictRecords);
            //更新记录
            eclExecutePredictRecordService.updateRecords(executePredictRecords);
            //通知预算更新
            //先按照计算月份分区

            Map<LocalDate, List<EclExecutePredictRecord>> month2RecordMap = executePredictRecords.stream().collect(Collectors.groupingBy(EclExecutePredictRecord::getCalculationDate));
            month2RecordMap.forEach((month, allPredictRecords) -> {
                BudgetEclRiskReserveBO riskReserveBO = new BudgetEclRiskReserveBO();
                Map<Long, Long> receiptId2RiskReserve = new HashMap<>();
                Map<Long, Long> receiptId2RiskReserveOverdue = new HashMap<>();
                riskReserveBO.setReceiptId2RiskReserve(receiptId2RiskReserve);
                riskReserveBO.setReceiptId2RiskReserveOverdue(receiptId2RiskReserveOverdue);
                riskReserveBO.setCalculationDate(month);
                Map<Long, BigDecimal> overdueMap = allPredictRecords.stream().filter(e -> ObjectUtil.equals(YesOrNoNumberEnum.YES.getCode(), e.getOverdueFlag())).collect(Collectors.toMap(EclExecutePredictRecord::getReceiptId, EclExecutePredictRecord::getEcl, (a, b) -> b));
                allPredictRecords.stream().filter(e -> !ObjectUtil.equals(YesOrNoNumberEnum.YES.getCode(), e.getOverdueFlag()) && ObjectUtil.isNotEmpty(e.getReceiptId())).forEach(e -> {
                    Long aLong = LongUtil.other2Long(e.getEcl() == null ? BigDecimal.ZERO.toPlainString() : e.getEcl().toPlainString());
                    receiptId2RiskReserve.put(e.getReceiptId(), aLong);
                    //补充逾期的
                    BigDecimal overdueBigDecimal = overdueMap.get(e.getReceiptId());
                    if (ObjectUtil.isNotEmpty(overdueBigDecimal)) {
                        receiptId2RiskReserveOverdue.put(e.getReceiptId(), LongUtil.other2Long(overdueBigDecimal.toPlainString()));
                    } else {
                        receiptId2RiskReserveOverdue.put(e.getReceiptId(), aLong);
                    }

                });
                riskReserveBOS.add(riskReserveBO);
            });
        } catch (Exception e) {
            isSuccess = false;
            log.error("EclExecutePredictBaseInfoService calculation error", e);
        } finally {
            //通知预算
            if (ObjectUtil.isNotEmpty(executePredictBaseInfo.getBudgetPlanId())){
                log.info("通知预算数据 {} {}", executePredictBaseInfo, riskReserveBOS);
                SpringContextHolder.getBean(BudgetPlanProfitService.class).refreshRiskFundBalanceByEcl(executePredictBaseInfo.getBudgetPlanId(), riskReserveBOS, isSuccess);
            }
        }
    }

    private void calculationByModel(List<EclExecutePredictRecord> records) {

        Map<String, String> innerLeave2OutLeaveMap = eclBusinessConfigService.innerLeave2OutLeave();
        EclBreachMappingBO eclBreachMappingBO = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.BREACH_MAPPING.name()).getConfigValue(), EclBreachMappingBO.class);

        //md评级对应pd
        List<EclBreachMappingBO.BreachMappingData> mdLeave2Pd = eclBreachMappingBO.getData();
        //查询调整因子Z
        Map<String, EclForwardZBO.EclForwardZBOData> group2ForwardZ = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.FORWARD_Z.name()).getConfigValue(), EclForwardZBO.class).getData().stream().collect(Collectors.toMap(EclForwardZBO.EclForwardZBOData::getGroup, e -> e, (a, b) -> a));

        //获取lgd
        Map<String, String> leaseType2Lgd = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.LOSS_LGD.name()).getConfigValue(), EclLossLgdBO.class).getData().stream().collect(Collectors.toMap(EclLossLgdBO.EclLossLgdData::getLeaseType, EclLossLgdBO.EclLossLgdData::getLgd, (a, b) -> a));

        //情景权重
        Map<String, BigDecimal> eclWeightMap = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.SCENARIO_WEIGHT.name()).getConfigValue(), EclScenarioWeightBO.class).getData().stream().collect(Collectors.toMap(EclScenarioWeightBO.EclScenarioWeightData::getScene, EclScenarioWeightBO.EclScenarioWeightData::getSceneWeight, (a, b) -> a));


        //获取内部评级
        Map<String, String> innerLeave2Pd = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.INNER_BREACH_MAPPING.name()).getConfigValue(), EclInnerBreachMappingBO.class).getData().stream().collect(Collectors.toMap(EclInnerBreachMappingBO.BreachMappingData::getInnerLevel,
                EclInnerBreachMappingBO.BreachMappingData::getInnerPd, (a, b) -> a));

        Map<String, String> clientName2Rating = new HashMap<>();

        //查询pd
        Map<String, String> ratingLeave2PdMap = eclBusinessConfigService.innerLeave2PD();

        LocalDateTime now = LocalDateTime.now();
        LocalDate localDateNow = LocalDate.now();

        //查询项目信息
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(records.stream().filter(e -> ObjectUtil.isNotEmpty(e.getContractId())).map(EclExecutePredictRecord::getContractId).collect(Collectors.toList()));
        Map<Long, Long> contractId2ReviewId = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId));
        //获取评估主体信息
        Map<Long, Long> reviewId2EvaluationSubjectId = projReviewBaseInfoLibHandler.listLatestByOriginIds(contractBaseInfos.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList()))
                .stream().filter(e -> ObjectUtil.isNotEmpty(e.getEvaluationSubjectId())).collect(Collectors.toMap(ProjReviewBaseInfoLib::getOriginId, ProjReviewBaseInfoLib::getEvaluationSubjectId, (a, b) -> b));
        Map<Long, Long> contractId2EvaluationSubjectId = new HashMap<>();
        contractId2ReviewId.forEach((k, v) -> {
            contractId2EvaluationSubjectId.put(k, reviewId2EvaluationSubjectId.get(v));
        });
        List<Long> clientIds = records.stream().filter(e -> ObjectUtil.isNotEmpty(e.getClientId())).map(EclExecutePredictRecord::getClientId).collect(Collectors.toList());
        clientIds.addAll(reviewId2EvaluationSubjectId.values());
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);

        Long clientId;

        //获取外部评级
        RatingManagementRSP domesticBaseLeaveR = null;
        try {
             domesticBaseLeaveR = ratingManagementClient.getDomesticBaseLeave(new ArrayList<>(clientId2Name.values())).getData();
        } catch (Exception e) {
            log.warn("获取外部评级异常", e);
        }
        if (ObjectUtil.isNotEmpty(domesticBaseLeaveR)) {
            clientName2Rating.putAll(domesticBaseLeaveR.getClientName2Rating());
        }

        //内部评级
        List<RatingClientLib> ratingClientLibList = SpringContextHolder.getBean(RatingClientLibService.class).list(Wrappers.<RatingClientLib>lambdaQuery()
                .in(RatingClientLib::getClientId, clientIds)
                .eq(RatingClientLib::getProcessStatus, ProcessStatus.APPROVAL_PASS.name()));
        // 评级信息
        Map<Long, List<RatingClientLib>> ratingClientLibMap = Optional.ofNullable(ratingClientLibList).map(m -> m.stream().collect(Collectors.groupingBy(RatingClientLib::getClientId))).orElse(Collections.emptyMap());

        //获取上迁结果
        Map<Long, Integer> promotionResult = SpringContextHolder.getBean(EclExecuteClientPromotionResultService.class).getPromotionResult(null, 6);


        for (EclExecutePredictRecord record : records) {
            try {
                clientId = contractId2EvaluationSubjectId.get(record.getContractId()) == null ? record.getClientId() : contractId2EvaluationSubjectId.get(record.getContractId());
                KpiExpectedLossDecisionQuery query = new KpiExpectedLossDecisionQuery();
                query.setOrder_num(record.getContractCode());
                query.setClientId(clientId);
                query.setClient_name(clientId == null ? record.getClientName() : clientId2Name.get(clientId));

                query.setClassify(record.getClassify());
                query.setExpire_day(record.getContractExpirationDate() == null ? null : record.getContractExpirationDate());
                if (ObjectUtil.isNotEmpty(query.getExpire_day())) {
                    query.setDate_difference(Math.max(ChronoUnit.DAYS.between(record.getCalculationDate() == null ? localDateNow : record.getCalculationDate(), query.getExpire_day()), 0));
                }
                query.setAccrued_interest(bigDecimalNull2Zero(record.getAccruedInterest()));
                query.setRemain_principal(bigDecimalNull2Zero(record.getRemainPrincipal()));
                query.setDeposit(bigDecimalNull2Zero(record.getDeposit()));
                query.setLease_type(record.getLeaseType());
                query.setLgd(leaseType2Lgd.get(query.getLease_type()));
                record.setLgd(query.getLgd());
                query.setBase_weight(eclWeightMap.get(BASE_SCENE));
                query.setOpt_weight(eclWeightMap.get(OPT_SCENE));
                query.setGlo_weight(eclWeightMap.get(GLO_SCENE));
                record.setPromotionResult(query.getPromotionResult());

                record.setEclParamWeight(JSONUtil.toJsonStr(ListUtil.toList(query.getBase_weight(), query.getOpt_weight(), query.getGlo_weight())));

                query.setEcl_breach_mapping_map(JSONUtil.toJsonStr(mdLeave2Pd));
                query.setInner_level(record.getInnerMdLevel());
                query.setGroup(record.getGroup());

                List<RatingClientLib> ratingClientLibs = ratingClientLibMap.get(clientId);

                if (CollectionUtil.isNotEmpty(ratingClientLibs)) {
                    RatingClientLib lastRatingClient = ratingClientLibs.stream().max(Comparator.comparing(RatingClientLib::getCreateTime)).orElse(new RatingClientLib());
                    RatingClientLib firstRatingClient = ratingClientLibs.stream().filter(f -> Objects.equals(f.getModelCode(), lastRatingClient.getModelCode())).min(Comparator.comparing(RatingClientLib::getCreateTime)).orElse(new RatingClientLib());
                    query.setInner_first_level(firstRatingClient.getFinalScore());
                    //计算下迁等级
                    EclOuterLevelEnum nowDisplay = EclOuterLevelEnum.getByDisplay(innerLeave2OutLeaveMap.get(query.getInner_level()));
                    EclOuterLevelEnum firstDisplay = EclOuterLevelEnum.getByDisplay(innerLeave2OutLeaveMap.get(query.getInner_first_level()));
                    //query.setInner_level(lastRatingClient.getFinalScore());
                    String pd = innerLeave2Pd.get(query.getInner_level());
                    query.setInner_pd(new BigDecimal(pd == null ? "0" : pd));
                   /* if (ObjectUtil.isEmpty(query.getGroup())) {
                        query.setGroup(Optional.of(KpiRatingModelGroupEnum.valueOf(lastRatingClient.getModelCode())).map(KpiRatingModelGroupEnum::getDisplay).orElse(null));
                    }*/
                    if (ObjectUtil.isNotEmpty(nowDisplay) && ObjectUtil.isNotEmpty(firstDisplay)) {
                        query.setRzy_ecl_down_level(Math.max(nowDisplay.getOrder() - firstDisplay.getOrder(), 0));
                    } else {
                        query.setRzy_ecl_down_level(0);
                    }
                } else if (ObjectUtil.isNotEmpty(query.getInner_level())) {
                    query.setRzy_ecl_down_level(0);
                }
                EclForwardZBO.EclForwardZBOData eclForwardZBO = group2ForwardZ.get(KpiRatingModelGroupEnum.client_hymx.display().equals(query.getGroup()) ? KpiRatingModelGroupEnum.client_fzzy_service.display() : query.getGroup());
                if (ObjectUtil.isNotEmpty(eclForwardZBO)) {
                    query.setRzy_ecl_base_z(eclForwardZBO.getFactorBaseZ());
                    query.setRzy_ecl_glo_z(eclForwardZBO.getFactorGloZ());
                    query.setRzy_ecl_opt_z(eclForwardZBO.getFactorOptZ());
                    record.setEclParamZ(JSONUtil.toJsonStr(ListUtil.toList(query.getRzy_ecl_base_z(), query.getRzy_ecl_opt_z(), query.getRzy_ecl_glo_z())));
                }
                record.setRzyEclDownLevel(query.getRzy_ecl_down_level());
                //补充外部信息
                query.setOuter_level(innerLeave2OutLeaveMap.get(clientName2Rating.get(query.getClient_name())));
                query.setEcl_out_pd(ratingLeave2PdMap.get(clientName2Rating.get(query.getClient_name())));
                //逾期天数
                query.setLate_day(Long.valueOf(record.getLateDay()));
                query.setLateDate(record.getLateDate());
                //上迁结果
                if (ObjectUtil.isNotEmpty(record.getPromotionResultHandle())) {
                    query.setPromotionResult(record.getPromotionResultHandle());
                } else {
                    query.setPromotionResult(promotionResult.getOrDefault(record.getContractId(), 0));
                }

                DecisionExecuteEclResult result = decisionService.eclExecute(query);
                if (result == null) {
                    log.error("本月风险金余额计算失败,id=" + record.getId());
                }
                //应计利息（只有业务类型不等于经营性租赁才有值）、下期租金（只有业务类型等于经营性租赁才有值）
                record.setEcl(result.getEcl());
                if (ObjectUtil.isNotEmpty(result)) {
                    record.setModelRecordKey(result.getTraceId());
                    record.setEvaluationSubjectId(clientId);
                    record.setEvaluationSubjectName(query.getClient_name());
                    record.setCalculationModelTime(now);
                    record.setOuterLevel(query.getOuter_level());
                    record.setEclOuterPd(query.getEcl_out_pd());
                    record.setInnerMdLevel(query.getInner_level());
                    record.setEclPd(query.getInner_pd() == null ? null : query.getInner_pd().toPlainString());
                    record.setLateDay(Math.toIntExact(query.getLate_day()));
                    Map<String, DecisionExecuteEclResult.OutputValue> outputMap = result.getOutputMap();
                    if (ObjectUtil.isNotEmpty(outputMap)) {
                        //record.setEclPd(getOutputValue(outputMap, ECL_PD_TEMP));
                        record.setEad(getOutputValueBigDecimal(outputMap, ECL_EAD));
                        record.setEclStep(getOutputValue(outputMap, ECL_STEP));
                        record.setEclFactorT(getOutputValueBigDecimal(outputMap, ECL_FACTOR_T));
                        record.setBasePdForward(getOutputValueBigDecimal(outputMap, BASE_PD_FORWARD));
                        record.setOptPdForward(getOutputValueBigDecimal(outputMap, OPT_PD_FORWARD));
                        record.setGloPdForward(getOutputValueBigDecimal(outputMap, GLO_PD_FORWARD));
                        record.setEclBaseIfrs9(getOutputValueBigDecimal(outputMap, ECL_BASE_IFRS9));
                        record.setEclOptIfrs9(getOutputValueBigDecimal(outputMap, ECL_OPT_IFRS9));
                        record.setEclGloIfrs9(getOutputValueBigDecimal(outputMap, ECL_GLO_IFRS9));
                        record.setBaseEcl(getOutputValueBigDecimal(outputMap, BASE_ECL));
                        record.setOptEcl(getOutputValueBigDecimal(outputMap, OPT_ECL));
                        record.setGloEcl(getOutputValueBigDecimal(outputMap, GLO_ECL));
                        record.setEcl(getOutputValueBigDecimal(outputMap, ECL));
                        record.setPromotionResult(getOutputValueBigDecimal(outputMap, ECL_STEP_PROMOTION).intValue());
                    }
                }
                //添加备注
                // 如确定债项所处阶段时，只命中了“从初始确认（数据初始化时）以来评级结果下迁大于等于四个等级”，则返回“下迁结果大于等于四个等级”。
                //如确定债项所处阶段时，处于三阶段，且合同对应主承租人风险敞口（客户维度）大于1亿，则返回“处于债项三阶段，且风险敞口大于1亿”。
                //如评级结果为C、CC、CCC且处于一阶段，则返回“评级结果为C、CC、CCC且处于一阶段”。
                if (ObjectUtil.isNotEmpty(record.getEclStep())) {
                    if (ObjectUtil.equals(Integer.valueOf(record.getEclStep()), record.getPromotionResult())) {
                        if (ObjectUtil.equals(record.getEclStep(), 3) && ObjectUtil.isNotEmpty(record.getRiskExposure()) && record.getRiskExposure().compareTo(new BigDecimal(ONE_HUNDRED_MILLION)) >= 0) {
                            record.setRemark("处于债项三阶段，且风险敞口大于1亿");
                        } else if (ObjectUtil.equals(record.getEclStep(), 1) && CCCList.contains(record.getInnerMdLevel())) {
                            record.setRemark("评级结果为C、CC、CCC且处于一阶段");
                        } else if (ObjectUtil.isNotEmpty(record.getRzyEclDownLevel()) && record.getRzyEclDownLevel() >= 4) {
                            record.setRemark("下迁结果大于等于四个等级");
                        } else {
                            record.setRemark("-");
                        }
                    } else if (ObjectUtil.isNotEmpty(record.getPromotionResult()) && record.getPromotionResult() > 0) {
                        record.setRemark("已上迁一级");
                    }
                }
            } catch (Exception e) {
                log.warn("计算减值模型error {}", record, e);
            }
        }
    }

    private BigDecimal bigDecimalNull2Zero(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
    }

    private String getOutputValue(Map<String, DecisionExecuteEclResult.OutputValue> outputValueMap, String code) {
        DecisionExecuteEclResult.OutputValue outputValue = outputValueMap.get(code);
        if (ECL_STEP.equals(code)) {
            Map<String, String> paramsValueMap = outputValue.getParamsValueMap();
            if (ObjectUtil.isNotEmpty(paramsValueMap) && ObjectUtil.isNotEmpty(paramsValueMap.get(ECL_STEP))) {
                return paramsValueMap.get(ECL_STEP);
            }
        }
        if (ObjectUtil.isNotEmpty(outputValue)) {
            return outputValue.getValue();
        }
        return null;
    }

    private BigDecimal getOutputValueBigDecimal(Map<String, DecisionExecuteEclResult.OutputValue> outputValueMap, String code) {
        DecisionExecuteEclResult.OutputValue outputValue = outputValueMap.get(code);
        if (ECL_STEP.equals(code)) {
            Map<String, String> paramsValueMap = outputValue.getParamsValueMap();
            if (ObjectUtil.isNotEmpty(paramsValueMap) && ObjectUtil.isNotEmpty(paramsValueMap.get(ECL_STEP))) {
                return new BigDecimal(paramsValueMap.get(ECL_STEP));
            }
        } else if (ObjectUtil.isNotEmpty(outputValue) && ObjectUtil.isNotEmpty(outputValue.getValue())) {
            return new BigDecimal(outputValue.getValue());
        }
        return BigDecimal.ZERO;
    }

}
