package cn.zswltech.mithras.service.service.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteAddREQ;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.budget.BudgetPlanPayFundPlanEnum;
import cn.zswltech.mithras.service.enums.budget.BudgetPlanTypeEnum;
import cn.zswltech.mithras.service.mapper.budget.BudgetExamineMapper;
import cn.zswltech.mithras.service.mapper.budget.BudgetExaminePayPlanExecuteMapper;
import cn.zswltech.mithras.service.mapper.budget.BudgetPlanMapper;
import cn.zswltech.mithras.service.mapper.model.budget.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算考核-投放计划执行情况表
* @author vico
* @date 2025-04-11
*/
@Service
public class BudgetExaminePayPlanExecuteService extends ServiceImpl<BudgetExaminePayPlanExecuteMapper, BudgetExaminePayPlanExecute> {

    @Resource
    private BudgetExaminePayPlanExecuteMapper budgetExaminePayPlanExecuteMapper;
    @Resource
    private Id2NameService id2NameService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(BudgetExaminePayPlanExecuteAddREQ req) {
        this.remove(Wrappers.<BudgetExaminePayPlanExecute>lambdaQuery()
                .eq(BudgetExaminePayPlanExecute::getBudgetExamineId, req.getBudgetExamineId()));
        BudgetExamine budgetExamine = SpringContextHolder.getBean(BudgetExamineMapper.class).selectById(req.getBudgetExamineId());
        if (ObjectUtil.isEmpty(budgetExamine)) {
            return;
        }
        //资金计划
        BudgetPlan budgetPlanMonth = SpringContextHolder.getBean(BudgetPlanMapper.class).selectOne(Wrappers.<BudgetPlan>lambdaQuery()
                .eq(BudgetPlan::getPlanYear, budgetExamine.getExamineYear())
                .eq(BudgetPlan::getPlanMonth, budgetExamine.getExamineMonth())
                .eq(BudgetPlan::getBudgetType, BudgetPlanTypeEnum.MONTH.name())
                .last(StringUtil.mysqlLimitOne()));
        BudgetPlan budgetPlanAdjustMonth = SpringContextHolder.getBean(BudgetPlanMapper.class).selectOne(Wrappers.<BudgetPlan>lambdaQuery()
                .eq(BudgetPlan::getPlanYear, budgetExamine.getExamineYear())
                .eq(BudgetPlan::getPlanMonth, budgetExamine.getExamineMonth())
                .eq(BudgetPlan::getBudgetType, BudgetPlanTypeEnum.MONTH_ADJUST.name())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(budgetPlanMonth) || ObjectUtil.isEmpty(budgetPlanAdjustMonth)) {
            throw new MithrasException("无月度计划/月度调整计划");
        }
        //本月周报计划
        List<BudgetPlanPayWeeklyReport> monthWeeks = SpringContextHolder.getBean(BudgetPlanPayWeeklyReportService.class).getMonthWeek(budgetExamine.getExamineYear(), budgetExamine.getExamineMonth());
        List<BudgetPlanPayWeeklyReportDetail> monthDetailWeeks = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(monthWeeks)) {
            monthDetailWeeks = SpringContextHolder.getBean(BudgetPlanPayWeeklyReportDetailService.class).list(Wrappers.<BudgetPlanPayWeeklyReportDetail>lambdaQuery()
                    .in(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId, monthWeeks.stream().map(BudgetPlanPayWeeklyReport::getId).collect(Collectors.toList())));
        }
        List<BudgetExaminePayPlanExecute> addList = new ArrayList<>();
        //查询资金投放计划金额
        Map<Long, Long> dept2FundsPlanAmount = getDept2FundsPlanAmount(budgetPlanMonth, budgetPlanAdjustMonth);
        //查询业务投放计划金额
        Map<Long, Long> dept2BusinessPlanAmount = getDept2BusinessPlanAmount(budgetPlanMonth, budgetPlanAdjustMonth);
        //当月付款核销金额
        Map<Long, Long> deptThisMonthPayment = getDeptThisMonthPayment(budgetPlanMonth.getPlanYear(), budgetPlanMonth.getPlanMonth());

        //获取项目本月付款核销项目
        Map<Long, Set<Long>> deptThisMonthReview = getDeptThisMonthReview(budgetPlanMonth.getPlanYear(), budgetPlanMonth.getPlanMonth());
        //获取项目月度投放计划调整版本项目
        Map<Long, Set<Long>> dept2BudgetPlanReview = getDept2BusinessPlanReview(budgetPlanAdjustMonth);

        //获取资金投放计划部门总提交日
        Map<Long, LocalDate> dept2FundsPlanDate = getDept2FundsPlanDate(budgetPlanMonth);
        Map<Long, LocalDate> dept2FundsPlanAdjustDate = getDept2FundsPlanDate(budgetPlanAdjustMonth);
        //获取周报部门逾期数
        Map<Long, Integer> dept2WeekDelayNum = getDept2WeekDelayNum(monthWeeks, monthDetailWeeks);
        Map<Long, Integer> dept2WeekDelayCount = getDept2WeekDelayCount(monthWeeks, monthDetailWeeks);

        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        orgList.sort(Comparator.comparing(OrgDO::getId));
        for (OrgDO org : orgList) {
            Long deptId = org.getId();
            BudgetExaminePayPlanExecute budgetExaminePayPlanExecute = new BudgetExaminePayPlanExecute();
            budgetExaminePayPlanExecute.setBudgetExamineId(req.getBudgetExamineId());
            budgetExaminePayPlanExecute.setBelongDeptId(deptId);
            Long fundsPlanAmount = dept2FundsPlanAmount.getOrDefault(deptId, 0L);
            Long businessPlanAmount = dept2BusinessPlanAmount.getOrDefault(deptId, 0L);
            Long thisMonthPayment = deptThisMonthPayment.getOrDefault(deptId, 0L);
            //分三段计算：
            //第一段：当月付款核销金额小于资金投放计划金额
            if (thisMonthPayment < LongUtil.null2zero(fundsPlanAmount)) {
                //偏离度=(当月付款核销金额/资金投放计划金额) -1
                if (fundsPlanAmount == 0) {
                    budgetExaminePayPlanExecute.setDeviationDegreePlan(0);
                } else {
                    BigDecimal b = (BigDecimal.valueOf(thisMonthPayment).multiply(BigDecimal.valueOf(1000000)).divide(BigDecimal.valueOf(fundsPlanAmount), 20, RoundingMode.HALF_UP)).subtract(BigDecimal.valueOf(1000000));
                    budgetExaminePayPlanExecute.setDeviationDegreePlan(Util.mithrasIntegerDecimalTwo(b.intValue()));
                }
            } else if (thisMonthPayment > LongUtil.null2zero(fundsPlanAmount) && thisMonthPayment < businessPlanAmount) { //第二段：当月付款核销金额大于等于资金投放计划金额，小于等于业务投放计划金额
                //偏离度=0
                budgetExaminePayPlanExecute.setDeviationDegreePlan(0);
            } else { //第三段：当月付款核销金额大于业务投放计划金额
                //偏离度=(当月付款核销金额/业务投放计划金额) - 1
                if (businessPlanAmount == 0) {
                    budgetExaminePayPlanExecute.setDeviationDegreePlan(0);
                } else {
                    BigDecimal b = BigDecimal.valueOf(thisMonthPayment).multiply(BigDecimal.valueOf(1000000)).divide(BigDecimal.valueOf(businessPlanAmount), 20, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1000000));
                    budgetExaminePayPlanExecute.setDeviationDegreePlan(Util.mithrasIntegerDecimalTwo(b.intValue()));
                }
            }
            //项目准确度 = 当月付款核销项目与月度投放计划调整版本重叠的数量（项目编号）/当月付款核销项目数量
            Set<Long> thisMonthReviews = deptThisMonthReview.getOrDefault(deptId, Collections.emptySet());
            Set<Long> budgetPlanReview = dept2BudgetPlanReview.getOrDefault(deptId, Collections.emptySet());
            int count = 0;
            for (Long e : thisMonthReviews) {
                if (budgetPlanReview.contains(e)){
                    count++;
                }
            }
            if (thisMonthReviews.isEmpty()) {
                budgetExaminePayPlanExecute.setProjectAccuracy(Util.mithrasIntegerDecimalTwo(0));
            } else {
                BigDecimal projectAccuracyBD = BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(10000)).divide(BigDecimal.valueOf(thisMonthReviews.size()), 20, RoundingMode.HALF_UP);
                budgetExaminePayPlanExecute.setProjectAccuracy(Util.mithrasIntegerDecimalTwo(projectAccuracyBD.intValue()));
            }
            //未及时提报次数-资金计划 部门总提交日-月度及调整计划截止日的差额大于0的次数（月度和调整的次数加和)
            int delayReportCount = 0;
            int delayDaysFundingPlan = 0;
            LocalDate fundsPlanDate = dept2FundsPlanDate.get(deptId);
            if (ObjectUtil.isNotEmpty(fundsPlanDate) && fundsPlanDate.isAfter(budgetPlanMonth.getCollectDateTo())) {
                ++delayReportCount;
                delayDaysFundingPlan = (int) (delayDaysFundingPlan + Math.max(0, ChronoUnit.DAYS.between(budgetPlanMonth.getCollectDateTo(), fundsPlanDate)));
            }
            LocalDate fundsPlanAdjustDate = dept2FundsPlanAdjustDate.get(deptId);
            if (ObjectUtil.isNotEmpty(fundsPlanAdjustDate) && fundsPlanAdjustDate.isAfter(budgetPlanAdjustMonth.getCollectDateTo())) {
                ++delayReportCount;
                delayDaysFundingPlan = (int) (delayDaysFundingPlan + Math.max(0, ChronoUnit.DAYS.between(budgetPlanAdjustMonth.getCollectDateTo(), fundsPlanAdjustDate)));
            }
            budgetExaminePayPlanExecute.setFailedReportFundingPlan(delayReportCount);
            //延迟天数-资金计划 部门总提交日-月度及调整计划截止日，小于0则按0算（月度和调整的天数加和）
            budgetExaminePayPlanExecute.setDelayDaysFundingPlan(delayDaysFundingPlan);
            //未及时提报次数-周报 = 部门总提交日-周报当周第一个工作日的前一个自然日的差额大于0的次数（每周的次数加和）
            budgetExaminePayPlanExecute.setFailedReportWeek(dept2WeekDelayNum.getOrDefault(deptId, 0));
            //延迟天数-周报 = 部门总提交日-周报当周第一个工作日的前一个自然日，小于0则按0算（每周的天数加和）
            budgetExaminePayPlanExecute.setDelayDaysWeek(dept2WeekDelayCount.getOrDefault(deptId, 0));
            addList.add(budgetExaminePayPlanExecute);
        }
        SpringContextHolder.getBean(BudgetExaminePayPlanExecuteService.class).saveBatch(addList);
    }

    //获取周报部门逾期数
    private Map<Long, Integer> getDept2WeekDelayNum(List<BudgetPlanPayWeeklyReport> monthWeeks, List<BudgetPlanPayWeeklyReportDetail> monthDetailWeeks) {
        Map<Long, Integer> map = new HashMap<>();
        if (ObjectUtil.isEmpty(monthWeeks) || ObjectUtil.isEmpty(monthDetailWeeks)) {
            return map;
        }
        Map<Long, List<BudgetPlanPayWeeklyReportDetail>> budgetPlanPayWeekId2Detail = monthDetailWeeks.stream().collect(Collectors.groupingBy(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId));
        for(BudgetPlanPayWeeklyReport weeklyReport : monthWeeks) {
            List<BudgetPlanPayWeeklyReportDetail> reportDetails = budgetPlanPayWeekId2Detail.get(weeklyReport.getId());
            if (ObjectUtil.isEmpty(reportDetails)) {
                continue;
            }
            Map<Long, List<BudgetPlanPayWeeklyReportDetail>> deptId2ReportDetail = reportDetails.stream().collect(Collectors.groupingBy(BudgetPlanPayWeeklyReportDetail::getBelongDeptId));
            deptId2ReportDetail.forEach((deptId, details) -> {
                LocalDate businessheadConfirmDate = details.get(0).getBusinessheadConfirmDate();
                if (ObjectUtil.isNotEmpty(businessheadConfirmDate) && businessheadConfirmDate.isAfter(weeklyReport.getDateFrom().minusDays(1))) {
                    map.put(deptId, map.getOrDefault(deptId, 0) + 1);
                }
            });

        }
        return map;
    }

    //获取周报部门逾期天数
    private Map<Long, Integer> getDept2WeekDelayCount(List<BudgetPlanPayWeeklyReport> monthWeeks, List<BudgetPlanPayWeeklyReportDetail> monthDetailWeeks) {
        Map<Long, Integer> map = new HashMap<>();
        if (ObjectUtil.isEmpty(monthWeeks) || ObjectUtil.isEmpty(monthDetailWeeks)) {
            return map;
        }
        Map<Long, List<BudgetPlanPayWeeklyReportDetail>> budgetPlanPayWeekId2Detail = monthDetailWeeks.stream().collect(Collectors.groupingBy(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId));
        for(BudgetPlanPayWeeklyReport weeklyReport : monthWeeks) {
            List<BudgetPlanPayWeeklyReportDetail> reportDetails = budgetPlanPayWeekId2Detail.get(weeklyReport.getId());
            if (ObjectUtil.isEmpty(reportDetails)) {
                continue;
            }
            Map<Long, List<BudgetPlanPayWeeklyReportDetail>> deptId2ReportDetail = reportDetails.stream().collect(Collectors.groupingBy(BudgetPlanPayWeeklyReportDetail::getBelongDeptId));
            deptId2ReportDetail.forEach((deptId, details) -> {
                LocalDate businessheadConfirmDate = details.get(0).getBusinessheadConfirmDate();
                if (ObjectUtil.isNotEmpty(businessheadConfirmDate) && businessheadConfirmDate.isAfter(weeklyReport.getDateFrom().minusDays(1))) {
                    map.put(deptId, (int) (map.getOrDefault(deptId, 0) + Math.max(0, ChronoUnit.DAYS.between(weeklyReport.getDateFrom().minusDays(1), businessheadConfirmDate))));
                }
            });
        }
        return map;
    }

    //查询资金投放计划金额
    private Map<Long, Long> getDept2FundsPlanAmount(BudgetPlan budgetPlanMonth, BudgetPlan budgetPlanAdjust) {
        Map<Long, Long> dept2FundsPlanAmountMap = new HashMap<>();
        List<BudgetPlanPayDetail> planPayDetails = SpringContextHolder.getBean(BudgetPlanPayDetailService.class).list(
                Wrappers.<BudgetPlanPayDetail>lambdaQuery()
                        .eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlanMonth.getId())
                        .in(BudgetPlanPayDetail::getBringIntoFundPlan, ListUtil.of(BudgetPlanPayFundPlanEnum.BRING_INTO.name(), BudgetPlanPayFundPlanEnum.BACKUP.name())));

        List<BudgetPlanPayDetail> budgetPlanAdjustDetail = SpringContextHolder.getBean(BudgetPlanPayDetailService.class).list(
                Wrappers.<BudgetPlanPayDetail>lambdaQuery()
                        .eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlanAdjust.getId())
                        .in(BudgetPlanPayDetail::getBringIntoFundPlan, ListUtil.of(BudgetPlanPayFundPlanEnum.BRING_INTO.name(), BudgetPlanPayFundPlanEnum.BACKUP.name())));

        if (ObjectUtil.isEmpty(planPayDetails)) {
            return dept2FundsPlanAmountMap;
        }
        Map<Long, List<BudgetPlanPayDetail>> deptId2PayDetail = planPayDetails.stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBelongDeptId));
        Map<Long, List<BudgetPlanPayDetail>> deptId2PayAdjustDetailMap = budgetPlanAdjustDetail.stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBelongDeptId));
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        for (OrgDO org : orgList) {
            long amount1 = Optional.ofNullable(deptId2PayDetail.get(org.getId())).map(list -> list.stream().filter(item -> Objects.nonNull(item.getPlanPayAmount())).mapToLong(BudgetPlanPayDetail::getPlanPayAmount).sum()).orElse(0L);
            long amount2 = Optional.ofNullable(deptId2PayAdjustDetailMap.get(org.getId())).map(list -> list.stream().filter(item -> Objects.nonNull(item.getPlanPayAmount())).mapToLong(BudgetPlanPayDetail::getPlanPayAmount).sum()).orElse(0L);
            BigDecimal b = BigDecimal.valueOf(amount1).multiply(BigDecimal.valueOf(0.2)).add(BigDecimal.valueOf(amount2).multiply(BigDecimal.valueOf(0.8)));
            dept2FundsPlanAmountMap.put(org.getId(), Util.mithrasLongDecimalTwo(b.longValue()));
        }
        return dept2FundsPlanAmountMap;
    }

    //查询
    private Map<Long, LocalDate> getDept2FundsPlanDate(BudgetPlan budgetPlan) {
        Map<Long, LocalDate> dept2FundsPlanAmountMap = new HashMap<>();
        List<BudgetPlanPayDetail> planPayDetails = SpringContextHolder.getBean(BudgetPlanPayDetailService.class).list(
                Wrappers.<BudgetPlanPayDetail>lambdaQuery()
                        .eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlan.getId()));
        if (ObjectUtil.isEmpty(planPayDetails)) {
            return dept2FundsPlanAmountMap;
        }
        Map<Long, List<BudgetPlanPayDetail>> deptId2PayDetail = planPayDetails.stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBelongDeptId));
        deptId2PayDetail.forEach((deptId, payDetails) -> {
            if (ObjectUtil.isNotEmpty(payDetails)) {
                dept2FundsPlanAmountMap.put(deptId, payDetails.get(0).getBusinessheadConfirmDate());
            }
        });
        return dept2FundsPlanAmountMap;
    }

    //查询业务投放计划金额
    private Map<Long, Long> getDept2BusinessPlanAmount(BudgetPlan budgetPlan, BudgetPlan budgetPlanAdjust) {
        Map<Long, Long> dept2FundsPlanAmountMap = new HashMap<>();
        List<BudgetPlanPayDetail> planPayDetails = SpringContextHolder.getBean(BudgetPlanPayDetailService.class).list(
                Wrappers.<BudgetPlanPayDetail>lambdaQuery().eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlan.getId()));
        if (ObjectUtil.isEmpty(planPayDetails)) {
            return dept2FundsPlanAmountMap;
        }
        List<BudgetPlanPayDetail> planPayAdjustDetails = SpringContextHolder.getBean(BudgetPlanPayDetailService.class).list(
                Wrappers.<BudgetPlanPayDetail>lambdaQuery().eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlanAdjust.getId()));
        Map<Long, List<BudgetPlanPayDetail>> deptId2PayDetail = planPayDetails.stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBelongDeptId));
        Map<Long, List<BudgetPlanPayDetail>> deptId2PayAdjustDetail = new HashMap<>();
        if (ObjectUtil.isNotEmpty(planPayAdjustDetails)) {
            deptId2PayAdjustDetail = planPayAdjustDetails.stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBelongDeptId));
        }

        for (Map.Entry<Long, List<BudgetPlanPayDetail>> entry : deptId2PayDetail.entrySet()) {
            Long deptId = entry.getKey();
            List<BudgetPlanPayDetail> payDetails = entry.getValue();
            if (ObjectUtil.isNotEmpty(payDetails)) {
                Long paidAmount = payDetails.stream().map(BudgetPlanPayDetail::getPaidAmount).filter(ObjectUtil::isNotNull).reduce(Long::sum).orElse(0L);
                Long planPayAmount = deptId2PayAdjustDetail.getOrDefault(deptId, new ArrayList<>()).stream().map(BudgetPlanPayDetail::getPlanPayAmount).filter(ObjectUtil::isNotNull).reduce(Long::sum).orElse(0L);
                dept2FundsPlanAmountMap.put(deptId, (long) (paidAmount * 0.2 + planPayAmount * 0.8));
            }
        }
        return dept2FundsPlanAmountMap;
    }

    //查询项目业务投放计划金额
    private Map<Long, Set<Long>> getDept2BusinessPlanReview(BudgetPlan budgetPlan) {
        Map<Long, Set<Long>> dept2BusinessPlanReviewMap = new HashMap<>();
        List<BudgetPlanPayDetail> planPayDetails = SpringContextHolder.getBean(BudgetPlanPayDetailService.class).list(
                Wrappers.<BudgetPlanPayDetail>lambdaQuery()
                        .eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlan.getId()));
        if (ObjectUtil.isEmpty(planPayDetails)) {
            return dept2BusinessPlanReviewMap;
        }
        Map<Long, List<BudgetPlanPayDetail>> deptId2PayDetail = planPayDetails.stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBelongDeptId));
        deptId2PayDetail.forEach((deptId, payDetails) -> {
            if (ObjectUtil.isNotEmpty(payDetails)) {
                dept2BusinessPlanReviewMap.put(deptId, payDetails.stream().map(BudgetPlanPayDetail::getProjReviewId).filter(ObjectUtil::isNotNull).collect(Collectors.toSet()));
            }
        });
        return dept2BusinessPlanReviewMap;
    }

    private Map<Long, Long> getDeptThisMonthPayment(Integer examineYear, Integer examineMonth) {
        LocalDate month = LocalDate.of(examineYear, examineMonth, 1);
        LocalDate nextMonth = month.plusMonths(1);
        Map<Long, Long> deptThisMonthPaymentMap = new HashMap<>();
        List<PaymentActualDetail> paymentActualDetails = SpringContextHolder.getBean(PaymentActualDetailService.class).list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .ge(PaymentActualDetail::getPaidInDate, month)
                .lt(PaymentActualDetail::getPaidInDate, nextMonth)
        );
        if (ObjectUtil.isEmpty(paymentActualDetails)) {
            return deptThisMonthPaymentMap;
        }
        Map<Long, List<PaymentActualDetail>> contractId2PaymentDetail = paymentActualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        //查询合同信息
        List<ContractBaseInfo> contractBaseInfos = SpringContextHolder.getBean(ContractBaseInfoService.class).listByIds(contractId2PaymentDetail.keySet());
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            deptThisMonthPaymentMap.put(contractBaseInfo.getBizDeptId(),
                    deptThisMonthPaymentMap.getOrDefault(contractBaseInfo.getBizDeptId(), 0L) + contractId2PaymentDetail.getOrDefault(contractBaseInfo.getId(), new ArrayList<>()).stream().map(PaymentActualDetail::getPaidInAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
        }
        return deptThisMonthPaymentMap;
    }

    private Map<Long, Set<Long>> getDeptThisMonthReview(Integer examineYear, Integer examineMonth) {
        LocalDate month = LocalDate.of(examineYear, examineMonth, 1);
        LocalDate nextMonth = month.plusMonths(1);
        Map<Long, Set<Long>> deptThisMonthReviewMap = new HashMap<>();
        List<PaymentActualDetail> paymentActualDetails = SpringContextHolder.getBean(PaymentActualDetailService.class).list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .ge(PaymentActualDetail::getPaidInDate, month)
                .lt(PaymentActualDetail::getPaidInDate, nextMonth)
        );
        if (ObjectUtil.isEmpty(paymentActualDetails)) {
            return deptThisMonthReviewMap;
        }
        Map<Long, List<PaymentActualDetail>> contractId2PaymentDetail = paymentActualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        //查询合同信息
        List<ContractBaseInfo> contractBaseInfos = SpringContextHolder.getBean(ContractBaseInfoService.class).listByIds(contractId2PaymentDetail.keySet());
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            Set<Long> orDefault = deptThisMonthReviewMap.getOrDefault(contractBaseInfo.getProjReviewId(), new HashSet<>());
            orDefault.add(contractBaseInfo.getProjReviewId());
            deptThisMonthReviewMap.put(contractBaseInfo.getBizDeptId(), orDefault);
        }
        return deptThisMonthReviewMap;
    }


    public List<BudgetExaminePayPlanExecuteListRSP> list(BudgetExaminePayPlanExecuteListREQ req) {
        //查询主表
        BudgetExamine budgetExamine = SpringContextHolder.getBean(BudgetExamineService.class).getById(req.getBudgetExamineId());
        if (ObjectUtil.isEmpty(budgetExamine)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //查询所有数据
        List<BudgetExaminePayPlanExecute> budgetExaminePayPlanExecutes = this.listByBudgetExamineId(req.getBudgetExamineId());
        if (ObjectUtil.isEmpty(budgetExaminePayPlanExecutes)) {
            return null;
        }
        String approvalStatus = budgetExamine.getApprovalStatus();
        //查询本年数据
        List<BudgetExamine> yearList = SpringContextHolder.getBean(BudgetExamineService.class).list(Wrappers.<BudgetExamine>lambdaQuery()
                .eq(BudgetExamine::getExamineYear, budgetExamine.getExamineYear())
                .le(BudgetExamine::getExamineMonth, budgetExamine.getExamineMonth()));
        Set<Integer> monthSet = yearList.stream().map(BudgetExamine::getExamineMonth).collect(Collectors.toSet());
        //年详情
        List<BudgetExaminePayPlanExecute> yearDetails = this.list(Wrappers.<BudgetExaminePayPlanExecute>lambdaQuery()
                .in(BudgetExaminePayPlanExecute::getBudgetExamineId, yearList.stream().map(BudgetExamine::getId).collect(Collectors.toList())));
        Map<Long, List<BudgetExaminePayPlanExecute>> yearDeptId2Details = yearDetails.stream().peek(e -> {
            // 扩大10000倍是为了配合前端统一除以10000的设定
            e.setFailedReportFundingPlan(Optional.ofNullable(e.getDelayDaysFundingPlan()).map(num -> num * 10000).orElse(null));
            e.setFailedReportWeek(Optional.ofNullable(e.getFailedReportWeek()).map(num -> num * 10000).orElse(null));
            e.setDelayDaysFundingPlan(Optional.ofNullable(e.getDelayDaysFundingPlan()).map(num -> num * 10000).orElse(null));
            e.setDelayDaysWeek(Optional.ofNullable(e.getDelayDaysWeek()).map(num -> num * 10000).orElse(null));
        }).collect(Collectors.groupingBy(BudgetExaminePayPlanExecute::getBelongDeptId));
        List<BudgetExaminePayPlanExecuteListRSP> rsps = new ArrayList<>();
        Map<Long, BudgetExaminePayPlanExecute> deptId2ExaminePayPlan = budgetExaminePayPlanExecutes.stream().peek(e -> {
            // 扩大10000倍是为了配合前端统一除以10000的设定
            e.setFailedReportFundingPlan(Optional.ofNullable(e.getDelayDaysFundingPlan()).map(num -> num * 10000).orElse(null));
            e.setFailedReportWeek(Optional.ofNullable(e.getFailedReportWeek()).map(num -> num * 10000).orElse(null));
            e.setDelayDaysFundingPlan(Optional.ofNullable(e.getDelayDaysFundingPlan()).map(num -> num * 10000).orElse(null));
            e.setDelayDaysWeek(Optional.ofNullable(e.getDelayDaysWeek()).map(num -> num * 10000).orElse(null));
        }).collect(Collectors.toMap(BudgetExaminePayPlanExecute::getBelongDeptId, e -> e, (a, b) -> a));
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptId2ExaminePayPlan.keySet());
        deptId2ExaminePayPlan.forEach((deptId, payPlan) -> {
            BudgetExaminePayPlanExecuteListRSP rsp = new BudgetExaminePayPlanExecuteListRSP();
            rsp.setDeptId(deptId);
            rsp.setApprovalStatus(approvalStatus);
            rsp.setDeptName(deptId2Name.get(deptId));
            //本月数
            rsp.setMonthDate(BeanUtil.copyProperties(payPlan, BudgetExaminePayPlanExecuteListRSP.BudgetExaminePayPlanExecuteListBody.class));
            //本年数
            List<BudgetExaminePayPlanExecute> yearBudgetExaminePayPlanExecutes = yearDeptId2Details.get(deptId);
            if (ObjectUtil.isNotEmpty(yearBudgetExaminePayPlanExecutes)) {
                BudgetExaminePayPlanExecuteListRSP.BudgetExaminePayPlanExecuteListBody yearPlan = new BudgetExaminePayPlanExecuteListRSP.BudgetExaminePayPlanExecuteListBody();
                yearPlan.setDeviationDegreePlan(yearBudgetExaminePayPlanExecutes.stream().map(BudgetExaminePayPlanExecute::getDeviationDegreePlan).map(LongUtil::null2zero).reduce(Integer::sum).orElse(0) / monthSet.size());
                yearPlan.setProjectAccuracy(yearBudgetExaminePayPlanExecutes.stream().map(BudgetExaminePayPlanExecute::getProjectAccuracy).map(LongUtil::null2zero).reduce(Integer::sum).orElse(0) / monthSet.size());
                yearPlan.setFailedReportFundingPlan(yearBudgetExaminePayPlanExecutes.stream().map(BudgetExaminePayPlanExecute::getFailedReportFundingPlan).map(LongUtil::null2zero).reduce(Integer::sum).orElse(0));
                yearPlan.setFailedReportWeek(yearBudgetExaminePayPlanExecutes.stream().map(BudgetExaminePayPlanExecute::getFailedReportWeek).map(LongUtil::null2zero).reduce(Integer::sum).orElse(0));
                yearPlan.setDelayDaysFundingPlan(yearBudgetExaminePayPlanExecutes.stream().map(BudgetExaminePayPlanExecute::getDelayDaysFundingPlan).map(LongUtil::null2zero).reduce(Integer::sum).orElse(0));
                yearPlan.setDelayDaysWeek(yearBudgetExaminePayPlanExecutes.stream().map(BudgetExaminePayPlanExecute::getDelayDaysWeek).map(LongUtil::null2zero).reduce(Integer::sum).orElse(0));
                rsp.setYearTotalDate(yearPlan);
            }
            rsps.add(rsp);
        });
        // 根据部门id排序
        rsps.sort(Comparator.comparing(BudgetExaminePayPlanExecuteListRSP::getDeptId));
        return rsps;
    }

    public List<BudgetExaminePayPlanExecute> listByBudgetExamineId(Long budgetExamineId) {
        return this.list(Wrappers.<BudgetExaminePayPlanExecute>lambdaQuery()
        .eq(BudgetExaminePayPlanExecute::getBudgetExamineId, budgetExamineId));
    }

}
