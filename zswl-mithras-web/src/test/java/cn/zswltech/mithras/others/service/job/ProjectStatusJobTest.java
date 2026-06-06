package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.projectprocess.job.ProjectStatusJob;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Slf4j
public class ProjectStatusJobTest {

    @Resource
    private ProjectStatusJob projectStatusJob;
    @Test
    public void projStatusJob() {
        projectStatusJob.projStatusJob();
    }

}
