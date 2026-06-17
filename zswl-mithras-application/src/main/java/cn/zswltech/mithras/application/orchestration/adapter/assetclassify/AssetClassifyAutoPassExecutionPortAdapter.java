package cn.zswltech.mithras.application.orchestration.adapter.assetclassify;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyAutoPassExecutionPort;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.ExecutionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class AssetClassifyAutoPassExecutionPortAdapter implements AssetClassifyAutoPassExecutionPort {

    @Resource
    private ExecutionService executionService;
    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public void passAllIfRunning(String processInstanceId, String message) {
        if (!isRunning(processInstanceId)) {
            return;
        }
        ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
        req.setProcessInstanceId(processInstanceId);
        req.setMessage(message);
        executionService.passAll(req);
    }

    @Override
    public void commitCurrentNodeIfRunning(String processInstanceId, String activityId, String handlerId, String message) {
        if (!isRunning(processInstanceId)) {
            return;
        }
        TaskSystemPageReq taskReq = new TaskSystemPageReq();
        taskReq.setIsRunning(1);
        taskReq.setProcessInstanceId(processInstanceId);
        taskReq.setSortType(1);
        Page<TaskResp> taskPage = flowTaskApiService.querySystemTask(taskReq);
        List<TaskResp> tasks = taskPage.getContents();
        if (ObjectUtil.isEmpty(tasks) || StringUtil.isEmpty(tasks.get(0).getTaskActivityId())
                || !Objects.equals(activityId, tasks.get(0).getTaskActivityId())) {
            return;
        }
        TaskResp task = tasks.get(0);
        executionService.currentNodeAutoCommit(task, handlerId, message);
    }

    private boolean isRunning(String processInstanceId) {
        ProcessResp processResp = flowTaskApiService.queryProcessById(processInstanceId);
        return Objects.equals(processResp.getProcessStatus(), ProcessBusinessStatusEnum.RUNNING.getType());
    }
}
