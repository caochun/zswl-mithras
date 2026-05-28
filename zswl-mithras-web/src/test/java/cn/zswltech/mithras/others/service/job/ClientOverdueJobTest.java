package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.service.job.ClientOverdueJob;
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
@ActiveProfiles("pre")
@Slf4j
public class ClientOverdueJobTest {

    @Resource
    private ClientOverdueJob clientOverdueJob;
    @Test
    public void clientPromotionByMonth() {
        clientOverdueJob.clientPromotionByMonth();
    }

}