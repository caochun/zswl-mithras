package cn.zswltech.mithras.workflow.controller.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.TaskApi;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import cn.zswltech.mithras.workflow.flow.TaskApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController implements TaskApi {
    @Resource
    private TaskApplicationService taskApplicationService;

    @Override
    public R<PageR<ProcessListRSP>> myProcessApplyingList(ProcessListREQ req) {
        return taskApplicationService.myProcessApplyingList(req);
    }

    @Override
    public R<PageR<TaskListRSP>> myProcessWithdrawList(TaskListREQ req) {
        return taskApplicationService.myProcessWithdrawList(req);
    }

    @Override
    public R<PageR<BackToStepTaskListRSP>> myProcessBackToStepList(TaskListREQ req) {
        return taskApplicationService.myProcessBackToStepList(req);
    }

    @Override
    public R<PageR<ProcessListRSP>> myProcessFinishList(ProcessListREQ req) {
        return taskApplicationService.myProcessFinishList(req);
    }

    @Override
    public R<MyProcessCountRSP> myProcessCount() {
        return taskApplicationService.myProcessCount();
    }

    @Override
    public R<PageR<ReceiveTaskListRSP>> myReceiveTodoList(TaskListREQ req) {
        return taskApplicationService.myReceiveTodoList(req);
    }

    @Override
    public R<PageR<ReceiveTaskListRSP>> myReceiveDoneList(TaskListREQ req) {
        return taskApplicationService.myReceiveDoneList(req);
    }

    @Override
    public R<PageR<ProcessCcListRSP>> myReceiveCcList(ProcessCcListREQ req) {
        return taskApplicationService.myReceiveCcList(req);
    }

    @Override
    public R<ReceiveProcessCountRSP> myReceiveCount() {
        return taskApplicationService.myReceiveCount();
    }

    @Override
    public R<TaskDetailRSP> taskDetail(TaskBaseREQ req) {
        return taskApplicationService.taskDetail(req);
    }

    @Override
    public R<ProcessDetailRSP> processDetail(ProcessBaseREQ req) {
        return taskApplicationService.processDetail(req);
    }

    @Override
    public R<List<ExecutionReturnableNodesRSP>> returnableNodes(@Valid ExecutionReturnableNodesREQ req) {
        return taskApplicationService.returnableNodes(req);
    }
}
