package cn.zswltech.mithras.workflow.flow.integration;

import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.mithras.foundation.port.ProcessStarter;
import cn.zswltech.mithras.foundation.port.ProcessVariableStarter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class FlowProcessStarter implements ProcessStarter, ProcessVariableStarter {

    @Resource
    private FlowProcessApiService flowProcessApiService;

    @Override
    public String start(String businessKey, Long startUserId, String modelKey, String processInstanceName) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(businessKey);
        startProcessReq.setStartUserId(String.valueOf(startUserId));
        startProcessReq.setModelKey(modelKey);
        startProcessReq.setProcessInstanceName(processInstanceName);
        return flowProcessApiService.start(startProcessReq);
    }

    @Override
    public String start(String businessKey, Long startUserId, Long startUserDeptId, String modelKey,
                        String processInstanceName, Map<String, Object> variables) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(businessKey);
        startProcessReq.setStartUserId(String.valueOf(startUserId));
        if (startUserDeptId != null) {
            startProcessReq.setStartUserDeptId(String.valueOf(startUserDeptId));
        }
        startProcessReq.setModelKey(modelKey);
        startProcessReq.setProcessInstanceName(processInstanceName);
        startProcessReq.setVariables(variables);
        return flowProcessApiService.start(startProcessReq);
    }
}
