package cn.zswltech.mithras.application.workflow.flow;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.NodeDefineResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.workflow.application.flow.api.TaskApplicationService;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.workflow.application.flow.service.ProcessService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程相关-任务-接口
 *
 * @author wangchuanhao
 * @date 2022/6/19 12:42 AM
 */
@Service
public class TaskFacade implements TaskApplicationService {

    @Resource
    private MyTaskService myTaskService;
    @Resource
    private FlowModelApiService flowModelApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ProcessService processService;

    @Override
    public R<PageR<ProcessListRSP>> myProcessApplyingList(ProcessListREQ req) {
        return R.ok(myTaskService.myProcessApplyingList(req));
    }

    @Override
    public R<PageR<TaskListRSP>> myProcessWithdrawList(TaskListREQ req) {
        return R.ok(myTaskService.myProcessWithdrawList(req));
    }

    @Override
    public R<PageR<BackToStepTaskListRSP>> myProcessBackToStepList(TaskListREQ req) {
        return R.ok(myTaskService.myProcessBackToStepList(req));
    }

    @Override
    public R<PageR<ProcessListRSP>> myProcessFinishList(ProcessListREQ req) {
        return R.ok(myTaskService.myProcessFinishList(req));
    }

    @Override
    public R<MyProcessCountRSP> myProcessCount() {
        return R.ok(myTaskService.myProcessCount());
    }

    @Override
    public R<PageR<ReceiveTaskListRSP>> myReceiveTodoList(TaskListREQ req) {
        return R.ok(myTaskService.myReceiveTodoList(req));
    }

    @Override
    public R<PageR<ReceiveTaskListRSP>> myReceiveDoneList(TaskListREQ req) {
        return R.ok(myTaskService.myReceiveDoneList(req));
    }

    @Override
    public R<PageR<ProcessCcListRSP>> myReceiveCcList(ProcessCcListREQ req) {
        return R.ok(myTaskService.myReceiveCcList(req));
    }

    @Override
    public R<ReceiveProcessCountRSP> myReceiveCount() {
        return R.ok(myTaskService.myReceiveCount());
    }

    @Override
    public R<TaskDetailRSP> taskDetail(TaskBaseREQ req) {
        return R.ok(myTaskService.taskDetail(req));
    }

    @Override
    public R<ProcessDetailRSP> processDetail(ProcessBaseREQ req) {
        return R.ok(myTaskService.processDetail(req));
    }

    /**
     * 查询可退回审批节点 此节点之前且已经走过的
     **/
    @Override
    public R<List<ExecutionReturnableNodesRSP>> returnableNodes(@Valid ExecutionReturnableNodesREQ req) {
        ProcessHistoryREQ processHistoryREQ = new ProcessHistoryREQ();
        processHistoryREQ.setProcessInstanceId(req.getProcessInstanceId());
        processHistoryREQ.setPage(1);
        processHistoryREQ.setPageSize(Integer.MAX_VALUE);
        PageR<ProcessHistoryRSP> history = processService.history(processHistoryREQ);
        TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
        Map<String, NodeDefineResp> nodeDefineMap = flowModelApiService.getNodeDefineListByProcessDefinitionId(taskResp.getProcessDefineId())
                .stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, n -> n));
        Set<String> beforeNodeIdSet = new HashSet<>();
        getBeforeNodeRecursion(nodeDefineMap, beforeNodeIdSet, taskResp.getTaskActivityId());
        List<ExecutionReturnableNodesRSP> rsps = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(history) && CollectionUtil.isNotEmpty(history.getList())) {
            Set<String> taskActivityIdSet = new HashSet<>();
            history.getList().forEach(node -> {
                if(ObjectUtil.isNotEmpty(node.getTaskActivityId()) && !taskActivityIdSet.contains(node.getTaskActivityId()) && beforeNodeIdSet.contains(node.getTaskActivityId())){
                    ExecutionReturnableNodesRSP rsp = new ExecutionReturnableNodesRSP();
                    rsp.setTaskActivityId(node.getTaskActivityId());
                    rsp.setTaskNodeName(node.getTaskNodeName());
                    rsps.add(rsp);
                    taskActivityIdSet.add(node.getTaskActivityId());
                }
            });
        }
        return R.ok(rsps);
    }

    //递归
    private void getBeforeNodeRecursion(Map<String, NodeDefineResp> nodeDefineMap, Set<String> beforeNodeIdSet, String curTaskActivityId){
        NodeDefineResp nodeDefineResp = nodeDefineMap.get(curTaskActivityId);
        if(ObjectUtil.isEmpty(nodeDefineResp)){
            return;
        }
        List<String> incomingIds = nodeDefineResp.getIncomingIds();
        if(ObjectUtil.isEmpty(incomingIds)){
            return;
        }
        beforeNodeIdSet.addAll(incomingIds);
        incomingIds.forEach(in -> getBeforeNodeRecursion(nodeDefineMap, beforeNodeIdSet, in));
    }


}
