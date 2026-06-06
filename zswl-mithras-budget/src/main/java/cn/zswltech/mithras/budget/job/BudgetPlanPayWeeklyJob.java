package cn.zswltech.mithras.budget.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.budget.application.job.BudgetPlanPayWeeklyJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@Component
public class BudgetPlanPayWeeklyJob {

    @Resource
    private BudgetPlanPayWeeklyJobService budgetPlanPayWeeklyJobService;

    @XxlJob(value = "createBudgetPlanPayWeekly")
    @Transactional(rollbackFor = Throwable.class)
    public void createBudgetPlanPayWeekly() {
        LocalDate targetDate;
        String params = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(params)) {
            targetDate = LocalDate.now();
        } else {
            targetDate = LocalDateTimeUtil.parseDate(params, DatePattern.NORM_DATE_PATTERN);
        }
        budgetPlanPayWeeklyJobService.createBudgetPlanPayWeekly(targetDate);
    }
}
