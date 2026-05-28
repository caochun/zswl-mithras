package cn.zswltech.mithras.others.bigbear;

import cn.zswltech.mithras.service.job.DashboardFinanceViewJob;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/9/3 17:07
 **/
@RunWith(SpringRunner.class)
@ActiveProfiles("uat")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardFinanceViewTest {

    @Resource
    private DashboardFinanceViewJob dashboardFinanceViewJob;

    @Test
    public void dashboardFinanceViewJob() {
        dashboardFinanceViewJob.dashboardFinanceViewJob();
    }
}
