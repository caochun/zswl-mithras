package cn.zswltech.mithras.others.job;

import cn.hutool.core.date.StopWatch;
import cn.zswltech.mithras.customer.application.client.ClientJobService;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Slf4j
public class ClientJobTest {

    @Resource
    private ClientJobService clientJobService;
    @Test
    public void clientAuthTypeModify() {
        StopWatch st = new StopWatch();
        st.start();
        clientJobService.clientAuthTypeModify();
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
    }

    @Test
    public void releaseClientJobTest() {
        clientJobService.releaseClient(null);
    }

}
