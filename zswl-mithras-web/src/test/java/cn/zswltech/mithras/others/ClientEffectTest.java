//import cn.zswltech.flow.core.api.FlowExecutionApiService;
//import cn.zswltech.flow.core.domain.req.execution.ExecutionBackToStepReq;
//import cn.zswltech.flow.core.domain.req.execution.ExecutionTaskBaseReq;
//import cn.zswltech.mithras.service.constant.FlowConstants;
//import cn.zswltech.mithras.service.enums.ClientVersionTypeEnum;
//import cn.zswltech.mithras.service.mapper.model.ClientVersion;
//import cn.zswltech.mithras.service.service.client.ClientService;
//import cn.zswltech.mithras.customer.application.lib.client.ClientVersionService;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.Resource;
//import java.util.Arrays;
//
///**
// * 客户生效逻辑
// *
// * @author wangchuanhao
// * @date 2022/6/22 12:27 PM
// */
//public class ClientEffectTest extends cn.zswltech.mithras.service.ApplicationTest {
//
//    private static final Logger log = LoggerFactory.getLogger(ClientEffectTest.class);
//
//    @Resource
//    private ClientService clientService;
//    @Resource
//    private FlowExecutionApiService executionApiService;
//    @Resource
//    private ClientVersionService clientVersionService;
//
//
//    @Test
//    public void effect() {
//        clientService.effect(8L);
//    }
//
//    @Test
//    public void canSave() {
//        log.info("客户是否可保存:{}", clientService.canSave(8L));
//    }
//
//    @Test
//    public void backToStep() {
//        ExecutionBackToStepReq req = new ExecutionBackToStepReq();
//        req.setTaskActivityId("userTask_startUser");
//        req.setHandlerId("1");
//        req.setMessage("xxx信息不完善，驳回补充材料");
//        req.setTaskId("9bb729eb-01d4-11ed-a3c7-6efa51fbcbc4");
//        executionApiService.backToStep(req);
//    }
//
//    @Test
//    public void pass() {
//        ExecutionTaskBaseReq req = new ExecutionTaskBaseReq();
//        req.setHandlerId("1");
//        req.setMessage("通过");
//        req.setTaskId("62f5e69c-028d-11ed-82a5-6efa51fbcbc4");
//        executionApiService.pass(req);
//    }
//
//    @Test
//    public void reject() {
//        ExecutionTaskBaseReq req = new ExecutionTaskBaseReq();
//        req.setHandlerId("1");
//        req.setMessage("审批拒绝");
//        req.setTaskId("bb3621e6-028b-11ed-82a5-6efa51fbcbc4");
//        executionApiService.reject(req);
//    }
//
//    @Test
//    public void recordVersion() {
//        clientVersionService.recordVersion(8L, ClientVersionTypeEnum.EFFECT, 2L);
//    }
//
//    @Test
//    public void reset() {
//        clientVersionService.reset(8L);
//    }
//
//    @Test
//    public void test() {
//        ClientVersion clientVersion = ClientVersion.builder()
//                .id(999L)
//                .clientId(1999L)
//                .type(1)
//                .version("123")
//                .build();
//        clientVersionService.saveBatch(Arrays.asList(clientVersion));
//    }
//
//}
