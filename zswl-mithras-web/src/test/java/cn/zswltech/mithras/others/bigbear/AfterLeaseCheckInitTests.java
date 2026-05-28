package cn.zswltech.mithras.others.bigbear;

import cn.zswltech.mithras.service.job.data_init.AfterLeaseCheckInitJob;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
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
 * @since 2025/9/21 16:50
 **/
@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AfterLeaseCheckInitTests {

    @Resource
    private AfterLeaseCheckInitJob afterLeaseCheckInitJob;

    @Test
    public void afterLeaseCheckInitGuarantorJob() {
        afterLeaseCheckInitJob.afterLeaseCheckInitGuarantorJob();
    }
}
