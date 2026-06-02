package cn.zswltech.mithras.service.service.flow;

import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.service.ProcessCcNotifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ExecutionProcessCcNotifier implements ProcessCcNotifier {

    @Resource
    private ExecutionApi executionApi;

    @Override
    public void cc(String processInstanceId, List<Long> userIds) {
        ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
        req.setProcessInstanceId(processInstanceId);
        req.setCcUserIdList(userIds);
        executionApi.cc(req);
    }
}
