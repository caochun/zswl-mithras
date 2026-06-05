package cn.zswltech.mithras.others.service.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.job.BudgetPlanPayWeeklyJob;
import cn.zswltech.mithras.basedata.util.WorkdayWeekUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("pre")
@Slf4j
public class BudgetPlanJobTest {

    @Resource
    private BudgetPlanPayWeeklyJob budgetPlanPayWeeklyJob;

    @Test
    public void workDayTest() {
        LocalDate date1 = LocalDate.of(2025,6,23);
        LocalDate date2 =LocalDate.of(2025,6,24);
        WorkdayWeekUtil.WorkweekResult result1 = WorkdayWeekUtil.calculateWorkweek(date1);
        WorkdayWeekUtil.WorkweekResult result2 = WorkdayWeekUtil.calculateWorkweek(date2);
        System.out.println(LocalDateTimeUtil.format(result1.getFirstWorkday(), DatePattern.NORM_DATE_PATTERN));
        System.out.println(LocalDateTimeUtil.format(result1.getLastWorkday(), DatePattern.NORM_DATE_PATTERN));
        System.out.println(LocalDateTimeUtil.format(result2.getFirstWorkday(), DatePattern.NORM_DATE_PATTERN));
        System.out.println(LocalDateTimeUtil.format(result2.getLastWorkday(), DatePattern.NORM_DATE_PATTERN));
    }


    @Test
    public void createBudgetPlanPayWeekly() {
        budgetPlanPayWeeklyJob.createBudgetPlanPayWeekly();
    }

}