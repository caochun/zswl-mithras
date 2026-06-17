package cn.zswltech.mithras.others.job;

import cn.zswltech.mithras.afterlease.job.AfterLeaseCheckPlanJob;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/11/21
 * @description
 */
@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class AfterLeaseCheckPlanJobTest {
    @Resource
    private AfterLeaseCheckPlanJob afterLeaseCheckPlanJob;

    @Test
    public void startCheckPlanTest() {
        afterLeaseCheckPlanJob.startCheckPlan();
    }

    @Test
    public void checkPlanToBeInitiated() {
        afterLeaseCheckPlanJob.checkPlanToBeInitiated();
    }

    @Test
    public void initCheckPlanToBeInitiated(){
//        afterLeaseCheckPlanJob.initCheckPlanToBeInitiated();
    }
}
