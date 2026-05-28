package cn.zswltech.mithras.others.client;

import cn.zswltech.mithras.service.service.client.ClientTransferService;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author yibin
 */
@ActiveProfiles("dev")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientTransferServiceTest {

    @Test
    void timedPass() {
        getBean(ClientTransferService.class).timedPass();
    }
}