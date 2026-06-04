package cn.zswltech.mithras.service.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportAddREQ;
import cn.zswltech.mithras.budget.domain.enums.BudgetPlanTypeEnum;
import cn.zswltech.mithras.budget.domain.enums.BudgetStatusEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.budget.*;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.WorkdayWeekUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class BudgetPlanPayWeeklyJob {
    @Resource
    private BudgetPlanService budgetPlanService;
    @Resource
    private BudgetPlanPayService budgetPlanPayService;
    @Resource
    private BudgetPlanPayDetailService budgetPlanPayDetailService;
    @Resource
    private BudgetPlanPayWeeklyReportService budgetPlanPayWeeklyReportService;
    @Resource
    private BudgetPlanPayWeeklyReportDetailService budgetPlanPayWeeklyReportDetailService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    @XxlJob(value = "createBudgetPlanPayWeekly")
    @Transactional(rollbackFor = Throwable.class)
    public void createBudgetPlanPayWeekly() {
        LocalDate targetDate;
        String params = XxlJobHelper.getJobParam();
//        String params = "2025-05-20";
        if (StrUtil.isBlank(params)) {
            targetDate = LocalDate.now();
        } else {
            targetDate = LocalDateTimeUtil.parseDate(params, DatePattern.NORM_DATE_PATTERN);
        }
        // 查询今天是否为第一个工作日
        if (!WorkdayWeekUtil.isDateFirstWorkday(targetDate)) {
            log.info("BudgetPlanPayWeeklyJob createBudgetPlanPayWeekly {} 非第一个工作日跳过", targetDate);
            return;
        }
        // 周报的数据来源有三个，1、月度投放计划。2、月度调整投放计划。3、上一周的周报。这三者取最新的数据
        BudgetPlan budgetPlan = budgetPlanService.getOne(
                Wrappers.<BudgetPlan>lambdaQuery()
                        .eq(BudgetPlan::getPlanYear, targetDate.getYear())
                        .eq(BudgetPlan::getPlanMonth, targetDate.getMonth())
                        .eq(BudgetPlan::getBudgetStatus, BudgetStatusEnum.CONFIRM.name())
                        .in(BudgetPlan::getBudgetType, ListUtil.of(BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name()))
                        .orderByDesc(BudgetPlan::getId)
                        .last(StringUtil.mysqlLimitOne())
        );
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getByBudgetPlanId(budgetPlan.getId());
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = SpringUtil.getBean(BudgetPlanPayWeeklyReportService.class).getOne(
                Wrappers.<BudgetPlanPayWeeklyReport>lambdaQuery()
                        .ge(BudgetPlanPayWeeklyReport::getDateFrom, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1))
                        .le(BudgetPlanPayWeeklyReport::getDateTo, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth()))
                        .eq(BudgetPlanPayWeeklyReport::getPlanStatus, BudgetStatusEnum.COLLECT_FINISH.name())
                        .orderByDesc(BudgetPlanPayWeeklyReport::getDateFrom)
                        .last(StringUtil.mysqlLimitOne())
        );
        List<BudgetPlanPayWeeklyReportDetail> todoList;
        if (Objects.isNull(budgetPlanPay) && Objects.isNull(budgetPlanPayWeeklyReport)) {
            log.info("没有投放计划也没有历史周报，不生成周报待办");
            return;
        } else if (Objects.isNull(budgetPlanPay)) {
            // 使用上一次周报的数据
            log.info("使用上一次周报数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPayWeeklyReport));
            List<BudgetPlanPayWeeklyReportDetail> budgetPlanPayWeeklyReportDetailList = budgetPlanPayWeeklyReportDetailService.listByReportId(budgetPlanPayWeeklyReport.getId());
            todoList = BeanUtil.copyToList(budgetPlanPayWeeklyReportDetailList, BudgetPlanPayWeeklyReportDetail.class);
        } else if (Objects.isNull(budgetPlanPayWeeklyReport)) {
            // 使用投放计划数据
            log.info("使用投放计划数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPay));
            List<BudgetPlanPayDetail> budgetPlanPayDetailList = budgetPlanPayDetailService.listByBudgetPlanId(budgetPlanPay.getBudgetPlanId());
            todoList = BeanUtil.copyToList(budgetPlanPayDetailList, BudgetPlanPayWeeklyReportDetail.class);
        } else {
            // 使用较新数据生成周报数据
            if (budgetPlanPay.getCreateTime().isAfter(budgetPlanPayWeeklyReport.getCreateTime())) {
                // 使用投放计划数据
                log.info("使用投放计划数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPay));
                List<BudgetPlanPayDetail> budgetPlanPayDetailList = budgetPlanPayDetailService.listByBudgetPlanId(budgetPlanPay.getBudgetPlanId());
                todoList = BeanUtil.copyToList(budgetPlanPayDetailList, BudgetPlanPayWeeklyReportDetail.class);
            } else {
                // 使用上一次周报的数据
                log.info("使用上一次周报数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPayWeeklyReport));
                List<BudgetPlanPayWeeklyReportDetail> budgetPlanPayWeeklyReportDetailList = budgetPlanPayWeeklyReportDetailService.listByReportId(budgetPlanPayWeeklyReport.getId());
                todoList = BeanUtil.copyToList(budgetPlanPayWeeklyReportDetailList, BudgetPlanPayWeeklyReportDetail.class);
            }
        }
        // 查询下周工作区间
        WorkdayWeekUtil.WorkweekResult workweekResult = WorkdayWeekUtil.calculateWorkweek(targetDate);
        // 只保留本月投放且实际投放小于计划
        todoList.removeIf(e -> this.isBeforeCurrentMonth(targetDate, e.getPlanPayDate()) || LongUtil.null2zero(e.getPaidAmount()) >= LongUtil.null2zero(e.getPlanPayAmount()));
        if (CollectionUtil.isEmpty(todoList)) {
            log.info("{}-{}不存在符合条件的待发周报", LocalDateTimeUtil.format(workweekResult.getFirstWorkday(), DatePattern.NORM_DATE_PATTERN), LocalDateTimeUtil.format(workweekResult.getLastWorkday(), DatePattern.NORM_DATE_PATTERN));
            return;
        }
        // 调整最新的主办
        Map<Long, Set<Long>> deptId2UserIds = new HashMap<>();
        for (BudgetPlanPayWeeklyReportDetail reportDetail : todoList) {
            // 查询最新的项目主办
            if (Objects.isNull(reportDetail.getProjReviewId())) {
                continue;
            }
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(reportDetail.getProjReviewId());
            if (Objects.nonNull(projReviewBaseInfo)) {
                reportDetail.setSponsorUserId(projReviewBaseInfo.getProjSponsorUserId());
                reportDetail.setBelongDeptId(projReviewBaseInfo.getBizDeptId());
            }
            Set<Long> userIds = deptId2UserIds.get(reportDetail.getBelongDeptId());
            if (Objects.isNull(userIds)) {
                userIds = new HashSet<>();
                deptId2UserIds.put(reportDetail.getBelongDeptId(), userIds);
            }
            userIds.add(reportDetail.getSponsorUserId());
        }
        // 创建周报
        BudgetPlanPayWeeklyReportAddREQ budgetPlanPayWeeklyReportAddREQ = BeanUtil.copyProperties(budgetPlanPay, BudgetPlanPayWeeklyReportAddREQ.class);
        budgetPlanPayWeeklyReportAddREQ.setDateFrom(workweekResult.getFirstWorkday());
        budgetPlanPayWeeklyReportAddREQ.setDateTo(workweekResult.getLastWorkday());
        budgetPlanPayWeeklyReportAddREQ.setPlanStatus(BudgetStatusEnum.COLLECTING.name());
        budgetPlanPayWeeklyReportAddREQ.setBudgetPlanPayId(budgetPlanPay.getId());
        Long budgetPlanPayWeeklyId = budgetPlanPayWeeklyReportService.create(budgetPlanPayWeeklyReportAddREQ, todoList);
        // 发送待办
        budgetPlanPayWeeklyReportService.createProcess(deptId2UserIds, budgetPlanPayWeeklyId);
    }

//    //发起周报
//    @XxlJob(value = "createBudgetPlanPayWeekly")
//    @Transactional(rollbackFor = Throwable.class)
//    public void createBudgetPlanPayWeekly() {
//        try {
//            LocalDate now = LocalDate.now();
//            //查询今天是否为第一个工作日
//            if (!WorkdayWeekUtil.isDateFirstWorkday(now)) {
//                log.info("BudgetPlanPayWeeklyJob createBudgetPlanPayWeekly {} 非第一个工作日跳过", now);
//                //return;
//            }
//            //查询下周工作区间
//            WorkdayWeekUtil.WorkweekResult workweekResult = WorkdayWeekUtil.calculateWorkweek(now);
//            //1.查询生效的月度计划
//            List<BudgetPlanPay> budgetPlanPays = budgetPlanPayService.list(Wrappers.<BudgetPlanPay>lambdaQuery()
//                    .eq(BudgetPlanPay::getBudgetType, BudgetPlanTypeEnum.MONTH.name())
//                    .eq(BudgetPlanPay::getBudgetStatus, BudgetStatusEnum.CONFIRM.name()));
//            if (ObjectUtil.isEmpty(budgetPlanPays)) {
//                return;
//            }
//            //todo 更新投放对应金额
//
//            //3.获取对应详情
//            Map<Long, List<BudgetPlanPayDetail>> planPayId2DetailMap = budgetPlanPayDetailService.list(Wrappers.<BudgetPlanPayDetail>lambdaQuery()
//                    .in(BudgetPlanPayDetail::getBudgetPlanPayId, budgetPlanPays.stream().map(BudgetPlanPay::getId).collect(Collectors.toList())))
//                    .stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBudgetPlanPayId));
//
//            budgetPlanPays.forEach(budgetPlanPay -> {
//                List<BudgetPlanPayDetail> budgetPlanPayDetails = planPayId2DetailMap.get(budgetPlanPay.getId());
//                if (ObjectUtil.isNotEmpty(budgetPlanPayDetails)) {
//                    Map<Long, Set<Long>> deptId2UserIds = new HashMap<>();
//                    budgetPlanPayDetails.forEach(detail -> {
//                        //本月投放且实际投放小于计划
//                        if (this.isInCurrentMonth(detail.getPlanPayDate()) && LongUtil.null2zero(detail.getPaidAmount()) < LongUtil.null2zero(detail.getPlanPayAmount())) {
//                            // 判断用户状态是否禁用
//                            UserDO userDO = SpringUtil.getBean(SysUserService.class).getSpecificUser(detail.getSponsorUserId());
//                            if (Objects.equals(userDO.getStatus(), YesOrNoNumberEnum.NO.getCode())) {
//                                //记录项目经理
//                                Set<Long> orDefault = deptId2UserIds.getOrDefault(detail.getBelongDeptId(), new HashSet<>());
//                                orDefault.add(detail.getSponsorUserId());
//                                deptId2UserIds.put(detail.getBelongDeptId(), orDefault);
//                            }
//                        }
//                    });
//                    if (ObjectUtil.isNotEmpty(deptId2UserIds)) {
//                        //创建周报
//                        BudgetPlanPayWeeklyReportAddREQ budgetPlanPayWeeklyReportAddREQ = BeanUtil.copyProperties(budgetPlanPay, BudgetPlanPayWeeklyReportAddREQ.class);
//                        budgetPlanPayWeeklyReportAddREQ.setDateFrom(workweekResult.getFirstWorkday());
//                        budgetPlanPayWeeklyReportAddREQ.setDateTo(workweekResult.getLastWorkday());
//                        budgetPlanPayWeeklyReportAddREQ.setPlanStatus(BudgetStatusEnum.COLLECTING.name());
//                        budgetPlanPayWeeklyReportAddREQ.setBudgetPlanPayId(budgetPlanPay.getId());
//                        Long budgetPlanPayWeeklyId = budgetPlanPayWeeklyReportService.create(budgetPlanPayWeeklyReportAddREQ, planPayId2DetailMap.get(budgetPlanPay.getId()));
//                        //发送待办
//                        budgetPlanPayWeeklyReportService.createProcess(deptId2UserIds, budgetPlanPayWeeklyId);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            log.warn("BudgetPlanPayWeeklyJob createBudgetPlanPayWeekly 执行异常", e);
//        }
//    }

    private boolean isBeforeCurrentMonth(LocalDate targetDate, LocalDate date) {
        // 获取当前年月和输入日期的年月进行比较
        if (ObjectUtil.isEmpty(date)) {
            return false;
        }
        LocalDate targetDateBegin = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1);
        return date.isBefore(targetDateBegin);
    }

}
