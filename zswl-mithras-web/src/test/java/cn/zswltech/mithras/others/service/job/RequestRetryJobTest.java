package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.service.job.RequestRetryJob;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */

@RunWith(SpringRunner.class)
//@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class RequestRetryJobTest {
    @Resource
    private RequestRetryJob requestRetryJob;

    @Test
    public void demoJobHandlerTest() {
        while (true){
            requestRetryJob.doJobHandler();
        }
    }
}
