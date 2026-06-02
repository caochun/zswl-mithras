package cn.zswltech.mithras.service.service.flow;

import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.mithras.service.service.ProcessStarter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FlowProcessStarter implements ProcessStarter {

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
}
