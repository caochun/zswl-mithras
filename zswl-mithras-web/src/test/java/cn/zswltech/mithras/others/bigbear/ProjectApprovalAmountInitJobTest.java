package cn.zswltech.mithras.others.bigbear;

import cn.zswltech.mithras.service.job.data_init.ProjectApprovalAmountInitJob;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/8/27 09:32
 **/
@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectApprovalAmountInitJobTest {

    @Autowired
    private ProjectApprovalAmountInitJob projectApprovalAmountInitJob;

    @Test
    public void projectApprovalAmountInitJob(){
        projectApprovalAmountInitJob.projectApprovalAmountInitJob();
    }
}
