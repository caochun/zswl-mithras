package cn.zswltech.mithras.service.service.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.domain.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.domain.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.budget.domain.enums.BudgetExamineBenefitEnum;
import cn.zswltech.mithras.budget.domain.enums.BudgetExamineBudgetExecuteEnum;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetExamineBudgetExecuteMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetExamineBenefit;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetExamineBudgetExecute;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.collection.service.bo.DeptRemainingPrincipalBO;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiPerformanceBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 预算管理-预算考核-预算执行情况表
 * @date 2025-04-11
 */
@Service
public class BudgetExamineBudgetExecuteService extends ServiceImpl<BudgetExamineBudgetExecuteMapper, BudgetExamineBudgetExecute> {
    @Resource
    private BudgetExamineBudgetExecuteMapper budgetExamineBudgetExecuteMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FinanceRiskHelp financeRiskHelp;

    private final static String REMARK_MODEL = "本月计提风险金%s万元，本年累计风险金余额%s万元；本月转回风险金%s万元";

    @Transactional(rollbackFor = Throwable.class)
    public void add(BudgetExamineBudgetExecuteAddREQ req) {
        //清理旧数据
        this.remove(Wrappers.<BudgetExamineBudgetExecute>lambdaQuery()
                .eq(BudgetExamineBudgetExecute::getBudgetExamineId, req.getBudgetExamineId()));
        List<BudgetExamineBudgetExecute> addList = new ArrayList<>();
        //查询上月数据
        LocalDate lastMonth = LocalDate.of(req.getBudgetExamineYear(), req.getBudgetExamineMonth(), 1).minusMonths(1);
        BudgetExamineDetailREQ budgetExamineDetailREQ = new BudgetExamineDetailREQ();
        budgetExamineDetailREQ.setExamineYear(lastMonth.getYear());
        budgetExamineDetailREQ.setExamineMonth(lastMonth.getMonthValue());
        BudgetExamineDetailRSP lastMonthMainBase = SpringContextHolder.getBean(BudgetExamineService.class).detail(budgetExamineDetailREQ);
        if (ObjectUtil.isEmpty(lastMonthMainBase)) {
            throw new MithrasException("上月考核表尚未创建，请先创建上一月的考核表");
        }
        Map<String, BudgetExamineBudgetExecute> lastMonthMap = this.list(Wrappers.<BudgetExamineBudgetExecute>lambdaQuery()
                .eq(BudgetExamineBudgetExecute::getBudgetExamineId, lastMonthMainBase.getId())).stream().collect(Collectors.toMap(e -> getDeptColumnKey(e.getBelongDeptId(), e.getFieldName()), e -> e, (a, b) -> a));
        // 上年同期数据
        Map<String, BudgetExamineBudgetExecute> lastYearMap;
        LocalDate lastYear = LocalDate.of(req.getBudgetExamineYear(), req.getBudgetExamineMonth(), 1).minusYears(1);
        budgetExamineDetailREQ.setExamineYear(lastYear.getYear());
        budgetExamineDetailREQ.setExamineMonth(lastYear.getMonthValue());
        BudgetExamineDetailRSP lastYearMainBase = SpringContextHolder.getBean(BudgetExamineService.class).detail(budgetExamineDetailREQ);
        if (Objects.nonNull(lastYearMainBase)) {
            lastYearMap = this.list(Wrappers.<BudgetExamineBudgetExecute>lambdaQuery().eq(BudgetExamineBudgetExecute::getBudgetExamineId, lastYearMainBase.getId())).stream().collect(Collectors.toMap(e -> getDeptColumnKey(e.getBelongDeptId(), e.getFieldName()), e -> e, (a, b) -> a));
        } else {
            lastYearMap = Collections.emptyMap();
        }
        // 获取全年预算目标
        List<PerformanceBaseInfo> performanceBaseInfoList = SpringUtil.getBean(KpiPerformanceBaseInfoService.class).list(
                Wrappers.<PerformanceBaseInfo>lambdaQuery()
                        .eq(PerformanceBaseInfo::getYear, req.getBudgetExamineYear())
                        .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.DEPARTMENT.name())
        );
        Map<Long, List<PerformanceBaseInfo>> performanceBaseInfoMap = performanceBaseInfoList.stream().collect(Collectors.groupingBy(PerformanceBaseInfo::getBelongDeptId));
        //投放金额
        addList.addAll(getPaymentAmountBudgetExecute(req));
        //考核表相关
        addList.addAll(getBenefitBudgetExecute(req));
        //苍穹相关
        addList.addAll(getCqData(req));
        // 立项数量
        addList.addAll(getProjectInfoBudgetExecute(req));
        // 添加加工数据
        LocalDate thisMonthDate = LocalDate.of(req.getBudgetExamineYear(), req.getBudgetExamineMonth(), 1);
        List<DeptRemainingPrincipalBO> deptRemainingPrincipalList = SpringUtil.getBean(CollectionBaseInfoMapper.class).calculateRemainingPrincipalGroupByDeptId(thisMonthDate.plusMonths(1));
        Map<Long, Long> deptRemainingPrincipalMap = deptRemainingPrincipalList.stream().collect(Collectors.toMap(DeptRemainingPrincipalBO::getBizDeptId, DeptRemainingPrincipalBO::getRemainingPrincipal));
        Map<Long, List<BudgetExamineBudgetExecute>> addMap = addList.stream().collect(Collectors.groupingBy(BudgetExamineBudgetExecute::getBelongDeptId));
        for (OrgDO org : SpringUtil.getBean(SysUserService.class).listBizDept()) {
            List<BudgetExamineBudgetExecute> executeList = addMap.get(org.getId());
            if (CollectionUtil.isEmpty(executeList)) {
                continue;
            }
            long amount = executeList.stream().filter(e -> StrUtil.equalsAny(e.getFieldName(), BudgetExamineBudgetExecuteEnum.INCLUDING_TRAVEL_EXPENSES.name(), BudgetExamineBudgetExecuteEnum.BUSINESS_ENTERTAINMENT_EXPENSES.name(), BudgetExamineBudgetExecuteEnum.CITY_TRANSPORTATION_EXPENSES.name())).mapToLong(BudgetExamineBudgetExecute::getTotalYear).sum();
            // 费用效率 = （差旅费+业务招待费+市内交通费）/立项个数
            long projectCount = executeList.stream().filter(e -> StrUtil.equals(e.getFieldName(), BudgetExamineBudgetExecuteEnum.CUMULATIVE_PROJECT_APPROVAL_COUNT.name())).mapToLong(BudgetExamineBudgetExecute::getTotalYear).sum();
            BudgetExamineBudgetExecute expenseEfficiencyExecute = new BudgetExamineBudgetExecute();
            expenseEfficiencyExecute.setBudgetExamineId(req.getBudgetExamineId());
            expenseEfficiencyExecute.setFieldName(BudgetExamineBudgetExecuteEnum.EXPENSE_EFFICIENCY.name());
            expenseEfficiencyExecute.setBelongDeptId(org.getId());
            if (projectCount != 0) {
                BigDecimal b = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(projectCount), 20, RoundingMode.HALF_UP);
                expenseEfficiencyExecute.setTotalYear(Util.mithrasLongDecimalTwo(b.longValue()));
            } else {
                expenseEfficiencyExecute.setTotalYear(0L);
            }
            addList.add(expenseEfficiencyExecute);
            // 费用水平率 = （差旅费+业务招待费+市内交通费）/营业收入
            long income = executeList.stream().filter(e -> StrUtil.equals(e.getFieldName(), BudgetExamineBudgetExecuteEnum.OPERATING_REVENUE.name())).mapToLong(BudgetExamineBudgetExecute::getTotalYear).sum();
            BudgetExamineBudgetExecute expenseLevelRate = new BudgetExamineBudgetExecute();
            expenseLevelRate.setBelongDeptId(org.getId());
            expenseLevelRate.setFieldName(BudgetExamineBudgetExecuteEnum.EXPENSE_LEVEL_RATE.name());
            expenseLevelRate.setTotalYear(division(amount, income, false));
            addList.add(expenseLevelRate);
            // 期末业务规模 = 各部门剩余本金对应月末的剩余本金
            BudgetExamineBudgetExecute endOfPeriodBusinessScale = new BudgetExamineBudgetExecute();
            endOfPeriodBusinessScale.setBelongDeptId(org.getId());
            endOfPeriodBusinessScale.setFieldName(BudgetExamineBudgetExecuteEnum.END_OF_PERIOD_BUSINESS_SCALE.name());
            endOfPeriodBusinessScale.setTotalYear(Optional.ofNullable(deptRemainingPrincipalMap.get(org.getId())).orElse(0L));
            addList.add(endOfPeriodBusinessScale);
        }
        Map<String, BudgetExamineBudgetExecute> map = addList.stream().collect(Collectors.toMap(e -> getDeptColumnKey(e.getBelongDeptId(), e.getFieldName()), e -> e));
        // 按照业务部门初始化一份数据（确保每个部门都有全量指标字段）
        List<OrgDO> allBizDept = SpringUtil.getBean(SysUserService.class).listBizDept();
        List<BudgetExamineBudgetExecute> initList = new LinkedList<>();
        for (OrgDO org : allBizDept) {
            for (BudgetExamineBudgetExecuteEnum item : BudgetExamineBudgetExecuteEnum.values()) {
                BudgetExamineBudgetExecute budgetExecute = new BudgetExamineBudgetExecute();
                budgetExecute.setBudgetExamineId(req.getBudgetExamineId());
                budgetExecute.setBelongDeptId(org.getId());
                budgetExecute.setFieldName(item.name());
                // 填充全年预算目标
                budgetExecute.setAnnualBudgetTarget(this.findAnnualBudgetTarget(item, performanceBaseInfoMap.get(org.getId())));
                initList.add(budgetExecute);
            }
        }
        // 填充具体数据并计算
        for (BudgetExamineBudgetExecute initExecute : initList) {
            BudgetExamineBudgetExecute calculateResult = map.get(getDeptColumnKey(initExecute.getBelongDeptId(), initExecute.getFieldName()));
            if (Objects.nonNull(calculateResult)) {
                initExecute.setCurrentMonth(Optional.ofNullable(calculateResult.getCurrentMonth()).orElse(0L));
                initExecute.setTotalYear(Optional.ofNullable(calculateResult.getTotalYear()).orElse(0L));
                initExecute.setLastYearPeriod(Optional.ofNullable(calculateResult.getLastYearPeriod()).orElse(0L));
            }
            // 获取上月数据
            BudgetExamineBudgetExecute lastMonthExecute = lastMonthMap.get(this.getDeptColumnKey(initExecute.getBelongDeptId(), initExecute.getFieldName()));
            // 获取上年数据
            BudgetExamineBudgetExecute lastYearExecute = lastYearMap.get(this.getDeptColumnKey(initExecute.getBelongDeptId(), initExecute.getFieldName()));
            if (!StrUtil.equalsAny(initExecute.getFieldName(), BudgetExamineBudgetExecuteEnum.EXPENSE_EFFICIENCY.name(), BudgetExamineBudgetExecuteEnum.EXPENSE_LEVEL_RATE.name())) {
                if (Objects.nonNull(lastMonthExecute)) {
                    //本月数 - 本月本年累计-上月本年累计
                    initExecute.setCurrentMonth(LongUtil.null2zero(initExecute.getTotalYear()) - LongUtil.null2zero(lastMonthExecute.getTotalYear()));
                }
            }
            if (ObjectUtil.isNotEmpty(lastYearExecute)) {
                //上年同期 - 取去年同期的预算执行情况表的本年累计
                initExecute.setLastYearPeriod(lastYearExecute.getTotalYear());
                //同比 = 本年累计/上年同期 - 1
                initExecute.setOnYear(division(initExecute.getTotalYear(), initExecute.getLastYearPeriod(), true));
            }
            // 进度预算目标 = 全年预算目标/12*月份
            initExecute.setProgressBudgetTarget(Util.mithrasLongDecimalTwo(BigDecimal.valueOf(initExecute.getAnnualBudgetTarget()).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(req.getBudgetExamineMonth())).longValue()));
            // 进度预算完成率（%） = 本年累计/进度预算目标
            if (!Objects.equals(initExecute.getProgressBudgetTarget(), 0L) && Objects.nonNull(initExecute.getTotalYear())) {
                initExecute.setProgressBudgetCompletionRate(Util.mithrasLongDecimalTwo(BigDecimal.valueOf(initExecute.getTotalYear()).divide(BigDecimal.valueOf(initExecute.getProgressBudgetTarget()), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000)).longValue()));
            }
            // 全年预算完成率（%）= 本年累计/全年预算目标
            if (!Objects.equals(initExecute.getAnnualBudgetTarget(), 0L) && Objects.nonNull(initExecute.getTotalYear())) {
                initExecute.setAnnualBudgetCompletionRate(Util.mithrasLongDecimalTwo(BigDecimal.valueOf(initExecute.getTotalYear()).divide(BigDecimal.valueOf(initExecute.getAnnualBudgetTarget()), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000)).longValue()));
            }
        }
        SpringContextHolder.getBean(BudgetExamineBudgetExecuteService.class).saveBatch(initList);
    }

    private long findAnnualBudgetTarget(BudgetExamineBudgetExecuteEnum item, List<PerformanceBaseInfo> performanceBaseInfoList) {
        if (CollectionUtil.isEmpty(performanceBaseInfoList)) {
            return 0L;
        }
        switch (item) {
            case NEW_INVESTMENT_AMOUNT: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getAdvertisingAmount()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getAdvertisingAmount).sum();
            case INCLUDING_OTHER_INDUSTRY_CATEGORY: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getAdvertisingAmount()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.INDUSTRY.name())).mapToLong(PerformanceBaseInfo::getAdvertisingAmount).sum();
            case PUBLIC_UTILITIES_CATEGORY: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getAdvertisingAmount()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.PLATFORM.name())).mapToLong(PerformanceBaseInfo::getAdvertisingAmount).sum();
            case STATE_OWNED_INDUSTRY_CATEGORY: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getAdvertisingAmount()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.FTP_STATE_OWNED_INDUSTRY.name())).mapToLong(PerformanceBaseInfo::getAdvertisingAmount).sum();
            case PEOPLE_LIVELIHOOD_CONSUMPTION_CATEGORY: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getAdvertisingAmount()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.FTP_CIVIL_CONSUMPTION.name())).mapToLong(PerformanceBaseInfo::getAdvertisingAmount).sum();
            case OPERATING_REVENUE: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getRevenueTarget()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getRevenueTarget).sum();
            case INCLUDING_CONSULTING_SERVICE_INCOME: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getConsultingFeeIncome()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getConsultingFeeIncome).sum();
            case INTEREST_INCOME: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getInterestIncome()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getInterestIncome).sum();
            case OPERATING_EXPENSES: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getBizFee()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getBizFee).sum();
            case INCLUDING_TRAVEL_EXPENSES: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getBusinessTripFee()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getBusinessTripFee).sum();
            case BUSINESS_ENTERTAINMENT_EXPENSES: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getBusinessServeFee()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getBusinessServeFee).sum();
            case ASSESSMENT_PROFIT: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getProfitTarget()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getProfitTarget).sum();
            case ASSESSMENT_PROFIT_BEFORE_ALLOWANCE: return performanceBaseInfoList.stream().filter(e -> Objects.nonNull(e.getBeforeProfitTarget()) && StrUtil.equals(e.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name())).mapToLong(PerformanceBaseInfo::getBeforeProfitTarget).sum();
            default: return 0L;
        }
    }

    private String getDeptColumnKey(Long deptId, String fieldName) {
        return String.format("%s-%s", deptId, fieldName);
    }

    private List<BudgetExamineBudgetExecute> getProjectInfoBudgetExecute(BudgetExamineBudgetExecuteAddREQ req) {
        LocalDate date = LocalDate.of(req.getBudgetExamineYear(), req.getBudgetExamineMonth(), 1);
        LocalDateTime queryStartTime = LocalDateTime.of(date.getYear(), 1, 1, 0, 0, 0);
        LocalDateTime queryEndTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.lengthOfMonth(), 23, 59, 59);
        List<Pair<Long, Long>> deptProjectCountList = SpringUtil.getBean(ProjEstablishBaseInfoMapper.class).countEffectProjectGroupByDept(queryStartTime, queryEndTime);
        Map<Long, Long> thisMonthMap = deptProjectCountList.stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        List<BudgetExamineBudgetExecute> result = new LinkedList<>();
        List<OrgDO> allBizDeptList = SpringUtil.getBean(SysUserService.class).listBizDept();
        for (OrgDO org : allBizDeptList) {
            BudgetExamineBudgetExecute projectCountExecute = new BudgetExamineBudgetExecute();
            projectCountExecute.setBudgetExamineId(req.getBudgetExamineId());
            projectCountExecute.setFieldName(BudgetExamineBudgetExecuteEnum.CUMULATIVE_PROJECT_APPROVAL_COUNT.name());
            projectCountExecute.setBelongDeptId(org.getId());
            projectCountExecute.setTotalYear(Optional.ofNullable(thisMonthMap.get(org.getId())).orElse(0L));
            result.add(projectCountExecute);
        }
        return result;
    }

    //投放相关
    private List<BudgetExamineBudgetExecute> getPaymentAmountBudgetExecute(BudgetExamineBudgetExecuteAddREQ req) {
        List<BudgetExamineBudgetExecute> rsps = new ArrayList<>();
        LocalDate localDate = LocalDate.of(req.getBudgetExamineYear(), req.getBudgetExamineMonth(), 1).plusMonths(1);
        //本年累计截止到本月已付款金额
        List<PaymentActualDetail> paymentActualDetails = SpringContextHolder.getBean(PaymentActualDetailService.class).list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .ge(PaymentActualDetail::getPaidInDate, LocalDate.of(req.getBudgetExamineYear(), 1, 1))
                .lt(PaymentActualDetail::getPaidInDate, localDate));
        if (ObjectUtil.isEmpty(paymentActualDetails)) {
            return rsps;
        }
        Map<Long, List<PaymentActualDetail>> contractId2PaymentActual = paymentActualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        //合同投放核销
        Map<Long, Long> contractId2PaymentAmount = new HashMap<>();
        contractId2PaymentActual.forEach((contractId, paymentActualList) -> {
            if (ObjectUtil.isNotEmpty(paymentActualList)) {
                contractId2PaymentAmount.put(contractId, paymentActualList.stream().map(PaymentActualDetail::getPaidInAmount).reduce(Long::sum).orElse(0L));
            }
        });
        //获取合同
        List<ContractBaseInfo> contractBaseInfos = SpringContextHolder.getBean(ContractBaseInfoService.class).listByIds(contractId2PaymentAmount.keySet());
        Map<Long, Long> contractId2ProjReviewId = contractBaseInfos.stream().filter(e -> StrUtil.isNotBlank(e.getProjCode())).collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId, (a, b) -> a));
        Map<Long, List<ContractBaseInfo>> projReviewId2Contract = contractBaseInfos.stream().filter(e -> StrUtil.isNotBlank(e.getProjCode())).collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        Map<Long, Long> contractId2DeptId = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getBizDeptId, (a, b) -> a));
        List<ProjPricingBaseInfo> projPricingBaseInfos = SpringContextHolder.getBean(ProjPricingBaseInfoService.class).list(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(projReviewId2Contract.keySet()), ProjPricingBaseInfo::getProjReviewId, projReviewId2Contract.keySet()));
        Map<Long, String> projReviewId2RiskType = new HashMap<>();
        if (ObjectUtil.isNotEmpty(projPricingBaseInfos)) {
            projReviewId2RiskType = projPricingBaseInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getProjCode()) && ObjectUtil.isNotEmpty(e.getFtpIndustryCategory())).collect(Collectors.toMap(ProjPricingBaseInfo::getProjReviewId, ProjPricingBaseInfo::getFtpIndustryCategory, (a, b) -> a));
        }
        // 投放按照部门分组
        Map<Long, List<PaymentActualDetail>> deptId2PaymentActual = new HashMap<>();
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : contractId2PaymentActual.entrySet()) {
            Long deptId = contractId2DeptId.get(entry.getKey());
            if (Objects.isNull(deptId)) {
                continue;
            }
            List<PaymentActualDetail> list = deptId2PaymentActual.get(deptId);
            if (Objects.isNull(list)) {
                list = new LinkedList<>();
                deptId2PaymentActual.put(deptId, list);
            }
            list.addAll(entry.getValue());
        }
        //新增投放额
        deptId2PaymentActual.forEach((deptId, paymentActualList) -> {
            BudgetExamineBudgetExecute rsp = new BudgetExamineBudgetExecute();
            rsp.setBelongDeptId(deptId);
            rsp.setFieldName(BudgetExamineBudgetExecuteEnum.NEW_INVESTMENT_AMOUNT.name());
            //本年累计 - 截止到月底的已付款核销金额
            rsp.setTotalYear(paymentActualList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum());
            rsps.add(rsp);
        });

        //分产业投放
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : deptId2PaymentActual.entrySet()) {
            Long deptId = entry.getKey();
            List<PaymentActualDetail> paymentActualDetailList = entry.getValue();
            // 拆分FTP行业分类的数据
            long ftpOther = 0L;
            long ftpPublic = 0L;
            long ftpCivil = 0L;
            long ftpState = 0L;
            for (PaymentActualDetail detail : paymentActualDetailList) {
                //获取产业分类
                String riskType = projReviewId2RiskType.get(contractId2ProjReviewId.get(detail.getContractId()));
                FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByName(riskType);
                if (ftpIndustryCategoryEnum == null) {
                    continue;
                }
                switch (ftpIndustryCategoryEnum) {
                    case FTP_OTHER_INDUSTRY:
                        ftpOther += detail.getPaidInAmount();
                        break;
                    case FTP_PUBLIC_UTILITIES:
                        ftpPublic += detail.getPaidInAmount();
                        break;
                    case FTP_STATE_OWNED_INDUSTRY:
                        ftpState += detail.getPaidInAmount();
                        break;
                    case FTP_CIVIL_CONSUMPTION:
                        ftpCivil += detail.getPaidInAmount();
                        break;
                    default:
                }
            }
            for (FtpIndustryCategoryEnum ftpIndustryCategoryEnum : FtpIndustryCategoryEnum.values()) {
                BudgetExamineBudgetExecute rsp = new BudgetExamineBudgetExecute();
                rsp.setBelongDeptId(deptId);
                rsp.setFieldName(this.ensureFtpFieldName(ftpIndustryCategoryEnum));
                //本年累计 - 截止到月底的已付款核销金额
                switch (ftpIndustryCategoryEnum) {
                    case FTP_OTHER_INDUSTRY:
                        rsp.setTotalYear(ftpOther);
                        break;
                    case FTP_PUBLIC_UTILITIES:
                        rsp.setTotalYear(ftpPublic);
                        break;
                    case FTP_STATE_OWNED_INDUSTRY:
                        rsp.setTotalYear(ftpState);
                        break;
                    case FTP_CIVIL_CONSUMPTION:
                        rsp.setTotalYear(ftpCivil);
                        break;
                    default:
                }
                rsps.add(rsp);
            }
        }
        return rsps;
    }

    private String ensureFtpFieldName(FtpIndustryCategoryEnum ftpIndustryCategoryEnum) {
        switch (ftpIndustryCategoryEnum) {
            case FTP_OTHER_INDUSTRY:
                return BudgetExamineBudgetExecuteEnum.INCLUDING_OTHER_INDUSTRY_CATEGORY.name();
            case FTP_PUBLIC_UTILITIES:
                return BudgetExamineBudgetExecuteEnum.PUBLIC_UTILITIES_CATEGORY.name();
            case FTP_STATE_OWNED_INDUSTRY:
                return BudgetExamineBudgetExecuteEnum.STATE_OWNED_INDUSTRY_CATEGORY.name();
            case FTP_CIVIL_CONSUMPTION:
                return BudgetExamineBudgetExecuteEnum.PEOPLE_LIVELIHOOD_CONSUMPTION_CATEGORY.name();
            default:
                return "";
        }
    }

    //考核相关
    private List<BudgetExamineBudgetExecute> getBenefitBudgetExecute(BudgetExamineBudgetExecuteAddREQ req) {
        List<BudgetExamineBudgetExecute> rsps = new ArrayList<>();
        List<BudgetExamineBenefit> budgetExamineBenefits = SpringContextHolder.getBean(BudgetExamineBenefitService.class).list(
                Wrappers.<BudgetExamineBenefit>lambdaQuery().eq(BudgetExamineBenefit::getBudgetExamineId, req.getBudgetExamineId()).eq(BudgetExamineBenefit::getFieldLevel, 3)
        );
        if (ObjectUtil.isEmpty(budgetExamineBenefits)) {
            return rsps;
        }
        Map<Long, List<BudgetExamineBenefit>> deptId2BudgetExamineBenefit = budgetExamineBenefits.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
        //不同类型数据
        deptId2BudgetExamineBenefit.forEach((deptId, budgetExamineBenefitList) -> {
            // 营业收入 = 效益考核表营业收入
            BudgetExamineBudgetExecute operatingRevenue = new BudgetExamineBudgetExecute();
            operatingRevenue.setBelongDeptId(deptId);
            operatingRevenue.setFieldName(BudgetExamineBudgetExecuteEnum.OPERATING_REVENUE.name());
            operatingRevenue.setTotalYear(budgetExamineBenefitList.stream().filter(e -> Objects.equals(e.getFieldName(), BudgetExamineBenefitEnum.OPERATING_REVENUE.name())).map(BudgetExamineBenefit::getFieldValue).reduce(Long::sum).orElse(0L));
            rsps.add(operatingRevenue);
            // 经营费用 = 效益考核表管理费用+销售费用+财务费用
            BudgetExamineBudgetExecute operatingExpense = new BudgetExamineBudgetExecute();
            operatingExpense.setBelongDeptId(deptId);
            operatingExpense.setFieldName(BudgetExamineBudgetExecuteEnum.OPERATING_EXPENSES.name());
            operatingExpense.setTotalYear(budgetExamineBenefitList.stream().filter(e -> StrUtil.equalsAny(e.getFieldName(), BudgetExamineBenefitEnum.DEDUCTION_SELLING_EXPENSES.name(), BudgetExamineBenefitEnum.DEDUCTION_ADMINISTRATIVE_EXPENSES.name(), BudgetExamineBenefitEnum.DEDUCTION_FINANCIAL_EXPENSES.name())).map(BudgetExamineBenefit::getFieldValue).reduce(Long::sum).orElse(0L));
            rsps.add(operatingExpense);
            // 考核利润 = 效益考核表考核利润总额
            BudgetExamineBudgetExecute assessmentProfit = new BudgetExamineBudgetExecute();
            assessmentProfit.setBelongDeptId(deptId);
            assessmentProfit.setFieldName(BudgetExamineBudgetExecuteEnum.ASSESSMENT_PROFIT.name());
            assessmentProfit.setTotalYear(budgetExamineBenefitList.stream().filter(e -> Objects.equals(e.getFieldName(), BudgetExamineBenefitEnum.ASSESSMENT_TOTAL_PROFIT.name())).map(BudgetExamineBenefit::getFieldValue).reduce(Long::sum).orElse(0L));
            rsps.add(assessmentProfit);
            // 考核利润拨备前 = 效益考核表考核利润总额 + 效益考核表风险准备金
            BudgetExamineBudgetExecute assessmentProfitBefore = new BudgetExamineBudgetExecute();
            assessmentProfitBefore.setBelongDeptId(deptId);
            assessmentProfitBefore.setFieldName(BudgetExamineBudgetExecuteEnum.ASSESSMENT_PROFIT_BEFORE_ALLOWANCE.name());
            assessmentProfitBefore.setTotalYear(budgetExamineBenefitList.stream().filter(e -> StrUtil.equalsAny(e.getFieldName(), BudgetExamineBenefitEnum.ASSESSMENT_TOTAL_PROFIT.name(), BudgetExamineBenefitEnum.RISK_PROVISION.name())).map(BudgetExamineBenefit::getFieldValue).reduce(Long::sum).orElse(0L));
            rsps.add(assessmentProfitBefore);
        });
        return rsps;
    }

    //苍穹相关
    private List<BudgetExamineBudgetExecute> getCqData(BudgetExamineBudgetExecuteAddREQ req) {
        List<BudgetExamineBudgetExecute> rsps = new ArrayList<>();
        List<BudgetExamineBudgetExecuteEnum> riskColumns = Arrays.stream(BudgetExamineBudgetExecuteEnum.values()).filter(e -> ObjectUtil.isNotEmpty(e.getRiskName())).collect(Collectors.toList());
        // 去掉经营费用，已经在效益考核表取过一次了
        riskColumns.removeIf(e -> e == BudgetExamineBudgetExecuteEnum.OPERATING_EXPENSES);
        //苍穹科目余额表
        Map<Long, Map<String, BigDecimal>> monthDeptValues = financeRiskHelp.getMonthDeptValues(req.getBudgetExamineYear(), req.getBudgetExamineMonth());
        if (ObjectUtil.isNotEmpty(monthDeptValues)) {
            monthDeptValues.forEach((deptId, riskNames) -> riskColumns.forEach(riskName -> {
                BudgetExamineBudgetExecute rsp = new BudgetExamineBudgetExecute();
                rsp.setBelongDeptId(deptId);
                //获取苍穹数据
                rsp.setFieldName(riskName.name());
                //填充值
                rsp.setTotalYear(getRiskValue(riskName.getRiskName(), riskNames));
                rsps.add(rsp);
            }));
        }
        return rsps;
    }

    private Long getRiskValue(String riskCode, Map<String, BigDecimal> riskMap) {
        return LongUtil.other2Long(financeRiskHelp.getRiskValue(riskMap, riskCode).toString());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(List<BudgetExamineBudgetExecuteModifyREQ> req) {
        List<BudgetExamineBudgetExecute> info = BeanUtil.copyToList(req, BudgetExamineBudgetExecute.class);
        this.updateBatchById(info);
    }

    public List<BudgetExamineBudgetExecuteListRSP> list(BudgetExamineBudgetExecuteListREQ req) {
        //1.查询为等级为0的
        List<BudgetExamineBudgetExecuteListRSP> rsps = new ArrayList<>();
        List<BudgetExamineBudgetExecute> budgetExamineBenefits = getSumBudgetExamineBudgetExecute(req);
        if (ObjectUtil.isNotEmpty(budgetExamineBenefits)) {
            Map<Long, List<BudgetExamineBudgetExecute>> deptId2Bean = budgetExamineBenefits.stream().map(e -> {
                if (Objects.equals(e.getFieldName(), BudgetExamineBudgetExecuteEnum.CUMULATIVE_PROJECT_APPROVAL_COUNT.name())) {
                    // 为了前端方便统一处理，累计立项个数也乘以10000后返回
                    e.setCurrentMonth(Optional.ofNullable(e.getCurrentMonth()).map(v -> v * 10000).orElse(0L));
                    e.setTotalYear(Optional.ofNullable(e.getTotalYear()).map(v -> v * 10000).orElse(0L));
                    e.setLastYearPeriod(Optional.ofNullable(e.getLastYearPeriod()).map(v -> v * 10000).orElse(0L));
                }
                return e;
            }).collect(Collectors.groupingBy(BudgetExamineBudgetExecute::getBelongDeptId));
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptId2Bean.keySet());
            //部门备注
            Map<Long, String> deptRemark = getDeptRemark(req.getBudgetExamineId());
            deptId2Bean.forEach((deptId, beans) -> {
                BudgetExamineBudgetExecuteListRSP rsp = new BudgetExamineBudgetExecuteListRSP();
                rsp.setBelongDeptId(deptId);
                rsp.setBelongDeptName(deptId2Name.get(deptId));
                if (ObjectUtil.isNotEmpty(beans)) {
                    rsp.setRowList(BeanUtil.copyToList(beans, BudgetExamineBudgetExecuteListRSP.BudgetExamineBudgetExecuteBody.class));
                }
                //添加备注
                //备注 = 本月计提风险金2401.74万元，本年累计风险金余额16965.94万元；本月转回风险金629.7万元
                rsp.setRemark(deptRemark.get(deptId));
                rsps.add(rsp);
            });
        }
        return sort(rsps);
    }

    private List<BudgetExamineBudgetExecuteListRSP> sort(List<BudgetExamineBudgetExecuteListRSP> rsps) {
        if (ObjectUtil.isEmpty(rsps)) {
            return rsps;
        }
        List<BudgetExamineBudgetExecuteEnum> columnEnums = Arrays.stream(BudgetExamineBudgetExecuteEnum.values()).filter(e -> e.getSort() > 0).sorted(Comparator.comparing(BudgetExamineBudgetExecuteEnum::getSort)).collect(Collectors.toList());

        rsps.forEach(rsp -> {
            rsp.setRowList(sortBody(rsp.getRowList(), columnEnums));
        });
        rsps.sort(Comparator.comparing(BudgetExamineBudgetExecuteListRSP::getBelongDeptId));
        return rsps;
    }

    private List<BudgetExamineBudgetExecuteListRSP.BudgetExamineBudgetExecuteBody> sortBody(List<BudgetExamineBudgetExecuteListRSP.BudgetExamineBudgetExecuteBody> currentMonthList, List<BudgetExamineBudgetExecuteEnum> budgetExecuteEnums) {
        if (ObjectUtil.isEmpty(currentMonthList)) {
            return currentMonthList;
        }
        List<BudgetExamineBudgetExecuteListRSP.BudgetExamineBudgetExecuteBody> orderList = new ArrayList<>();
        Map<String, BudgetExamineBudgetExecuteListRSP.BudgetExamineBudgetExecuteBody> name2Map = currentMonthList.stream().collect(Collectors.toMap(BudgetExamineBudgetExecuteListRSP.BudgetExamineBudgetExecuteBody::getFieldName, e -> e, (a, b) -> a));
        budgetExecuteEnums.forEach(e -> {
            orderList.add(name2Map.get(e.name()));
        });
        return orderList;
    }

    public List<BudgetExamineBudgetExecute> getSumBudgetExamineBudgetExecute(BudgetExamineBudgetExecuteListREQ req) {
        return budgetExamineBudgetExecuteMapper.selectList(
                Wrappers.<BudgetExamineBudgetExecute>lambdaQuery()
                .eq(BudgetExamineBudgetExecute::getBudgetExamineId, req.getBudgetExamineId())
                .orderByAsc(BudgetExamineBudgetExecute::getBelongDeptId)
        );
    }

    private Map<Long, String> getDeptRemark(Long budgetExamineId) {
        Map<Long, String> rsps = new HashMap<>();
        //本月考核数据
        List<BudgetExamineBenefit> budgetExamineBenefits = SpringContextHolder.getBean(BudgetExamineBenefitService.class).list(Wrappers.<BudgetExamineBenefit>lambdaQuery()
                .eq(BudgetExamineBenefit::getBudgetExamineId, budgetExamineId)
                .in(BudgetExamineBenefit::getFieldLevel, 2, 3));
        if (ObjectUtil.isEmpty(budgetExamineBenefits)) {
            return rsps;
        }
        LocalDate lastDate = LocalDate.of(budgetExamineBenefits.get(0).getBudgetExamineYear(), budgetExamineBenefits.get(0).getBudgetExamineMonth(), 1).minusMonths(1);
        //获取上月数据
        List<BudgetExamineBenefit> lastBenefit = SpringContextHolder.getBean(BudgetExamineBenefitService.class).list(Wrappers.<BudgetExamineBenefit>lambdaQuery()
                .eq(BudgetExamineBenefit::getBudgetExamineYear, lastDate.getYear())
                .eq(BudgetExamineBenefit::getBudgetExamineMonth, lastDate.getMonthValue())
                .eq(BudgetExamineBenefit::getFieldName, BudgetExamineBenefitEnum.RISK_PROVISION.name())
                .in(BudgetExamineBenefit::getFieldLevel, 2));
        Map<Long, List<BudgetExamineBenefit>> deptId2Last = new HashMap<>();
        if (ObjectUtil.isNotEmpty(lastBenefit)) {
            deptId2Last = lastBenefit.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
        }
        Map<Long, List<BudgetExamineBenefit>> deptId2BudgetExamineBenefit = budgetExamineBenefits.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
        Map<Long, List<BudgetExamineBenefit>> finalDeptId2Last = deptId2Last;
        deptId2BudgetExamineBenefit.forEach((deptId, examineBenefits) -> {
            if (ObjectUtil.isEmpty(examineBenefits)) {
                return;
            }
            Long monthProvision = 0L;
            Long yearProvision = 0L;
            Long monthProvisionLast = 0L;
            Long yearProvisionLast = 0L;
            List<BudgetExamineBenefit> lastExamineBenefits = finalDeptId2Last.get(deptId);
            if (ObjectUtil.isNotEmpty(lastExamineBenefits)) {
                List<BudgetExamineBenefit> monthExamineBenefitLast = lastExamineBenefits.stream().filter(e -> ObjectUtil.equals(2, e.getFieldLevel())).collect(Collectors.toList());
                List<BudgetExamineBenefit> yearExamineBenefitLast = lastExamineBenefits.stream().filter(e -> ObjectUtil.equals(3, e.getFieldLevel())).collect(Collectors.toList());
                if (ObjectUtil.isNotEmpty(monthExamineBenefitLast)){
                    monthProvisionLast = monthExamineBenefitLast.stream().map(BudgetExamineBenefit::getFieldValue).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L);
                }
                if(ObjectUtil.isNotEmpty(yearExamineBenefitLast)) {
                    yearProvisionLast = yearExamineBenefitLast.stream().map(BudgetExamineBenefit::getFieldValue).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L);
                }
            }
            List<BudgetExamineBenefit> monthExamineBenefit = examineBenefits.stream().filter(e -> ObjectUtil.equals(2, e.getFieldLevel())).collect(Collectors.toList());
            List<BudgetExamineBenefit> yearExamineBenefit = examineBenefits.stream().filter(e -> ObjectUtil.equals(3, e.getFieldLevel())).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(monthExamineBenefit)){
                Map<String, Long> map = monthExamineBenefit.stream().filter(e -> ObjectUtil.isNotEmpty(e.getFieldValue())).collect(Collectors.toMap(BudgetExamineBenefit::getFieldName, BudgetExamineBenefit::getFieldValue, (a, b) -> a));
                monthProvision += map.getOrDefault(BudgetExamineBenefitEnum.RISK_PROVISION.name(), 0L);
            }
            if(ObjectUtil.isNotEmpty(yearExamineBenefit)) {
                Map<String, Long> map = yearExamineBenefit.stream().filter(e -> ObjectUtil.isNotEmpty(e.getFieldValue())).collect(Collectors.toMap(BudgetExamineBenefit::getFieldName, BudgetExamineBenefit::getFieldValue, (a, b) -> a));
                yearProvision += map.getOrDefault(BudgetExamineBenefitEnum.ENDING_BALANCE.name(), 0L);
            }
            String remark = String.format(REMARK_MODEL, getTenThousand(monthProvision), getTenThousand(yearProvision), getTenThousand(monthProvisionLast - monthProvision));
            rsps.put(deptId, remark);
        });
        return rsps;
    }
    private String getTenThousand(Long num) {
        if (ObjectUtil.isEmpty(num)) {
            return "0";
        }
        return new BigDecimal(num).divide(new BigDecimal(100000000), 2, RoundingMode.HALF_UP).toPlainString();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(BudgetExamineBudgetExecuteRemoveREQ req) {
        BudgetExamineBudgetExecute originalInfo = budgetExamineBudgetExecuteMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        budgetExamineBudgetExecuteMapper.deleteById(req.getId());
    }

    private Long division(Long numerator, Long denominator, boolean needMinusOne) {
        if (ObjectUtil.isEmpty(numerator) || ObjectUtil.isEmpty(denominator) || denominator == 0) {
            return 0L;
        }
        BigDecimal b = BigDecimal.valueOf(numerator).multiply(BigDecimal.valueOf(1000000)).divide(BigDecimal.valueOf(denominator), 20, RoundingMode.HALF_UP);
        if (needMinusOne) {
            b = b.subtract(BigDecimal.valueOf(1000000));
            return Util.mithrasLongDecimalTwo(b.longValue());
        }
        return Util.mithrasLongDecimalTwo(b.longValue());
    }

}