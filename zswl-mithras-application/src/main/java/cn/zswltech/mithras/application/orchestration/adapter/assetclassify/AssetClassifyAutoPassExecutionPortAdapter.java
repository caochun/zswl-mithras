package cn.zswltech.mithras.application.orchestration.adapter.assetclassify;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.mithras.assetclassify.application.job.AssetClassifyAutoPassExecutionPort;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.ExecutionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AssetClassifyAutoPassExecutionPortAdapter implements AssetClassifyAutoPassExecutionPort {

    @Resource
    private ExecutionService executionService;

    @Override
    public void passAll(String processInstanceId, String message) {
        ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
        req.setProcessInstanceId(processInstanceId);
        req.setMessage(message);
        executionService.passAll(req);
    }

    @Override
    public void currentNodeAutoCommit(TaskResp task, String handlerId, String message) {
        executionService.currentNodeAutoCommit(task, handlerId, message);
    }
}
