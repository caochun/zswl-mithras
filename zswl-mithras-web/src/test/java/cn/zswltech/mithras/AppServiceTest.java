package cn.zswltech.mithras;

import cn.zswltech.mithras.customer.application.app.VisitDownloadTask;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author dingqi
 * @date 2025/9/6
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("uat")
public class AppServiceTest {

    @Test
    public void batchDownloadTest() {
        VisitDownloadTask visitDownloadTask = new VisitDownloadTask(3L);
        visitDownloadTask.run();
    }
}
