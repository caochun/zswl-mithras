package cn.zswltech.mithras.others.client;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.client.ClientService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2024/9/20
 * @description
 */
public class ClientServiceTest extends ApplicationTest {
    @Resource
    private ClientService clientService;

    @Test
    public void releaseClientTest() {
        clientService.tryReleaseClient(clientService.getById(4611L));
    }
}
