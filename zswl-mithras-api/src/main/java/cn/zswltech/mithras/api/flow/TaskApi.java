package cn.zswltech.mithras.api.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * 流程相关-任务-接口
 *
 * @author wangchuanhao
 * @date 2022/6/22 10:08 PM
 */
@Api(tags = "流程相关-任务-接口")
@RequestMapping("/flow/task")
public interface TaskApi {

    @ApiOperation("我发起的-申请中（流程）")
    @PostMapping("/myProcess/applying/list")
    R<PageR<ProcessListRSP>> myProcessApplyingList(@RequestBody @Valid ProcessListREQ req);

    @ApiOperation("我发起的-我的撤回")
    @PostMapping("/myProcess/withdraw/list")
    R<PageR<TaskListRSP>> myProcessWithdrawList(@RequestBody @Valid TaskListREQ req);

    @ApiOperation("我发起的-审批退回")
    @PostMapping("/myProcess/backToStep/list")
    R<PageR<BackToStepTaskListRSP>> myProcessBackToStepList(@RequestBody @Valid TaskListREQ req);

    @ApiOperation("我发起的-已结束（流程）")
    @PostMapping("/myProcess/finish/list")
    R<PageR<ProcessListRSP>> myProcessFinishList(@RequestBody @Valid ProcessListREQ req);

    @ApiOperation("我发起的-数量")
    @GetMapping("/myProcess/count")
    R<MyProcessCountRSP> myProcessCount();

    @ApiOperation("我收到的-待审批")
    @PostMapping("/myReceive/todo/list")
    R<PageR<ReceiveTaskListRSP>> myReceiveTodoList(@RequestBody @Valid TaskListREQ req);

    @ApiOperation("我收到的-已审批")
    @PostMapping("/myReceive/done/list")
    R<PageR<ReceiveTaskListRSP>> myReceiveDoneList(@RequestBody @Valid TaskListREQ req);

    @ApiOperation("我收到的-抄送我的")
    @PostMapping("/myReceive/cc/list")
    R<PageR<ProcessCcListRSP>> myReceiveCcList(@RequestBody @Valid ProcessCcListREQ req);

    @ApiOperation("我收到的-数量")
    @GetMapping("/myReceive/count")
    R<ReceiveProcessCountRSP> myReceiveCount();

    @ApiOperation("任务详情（列表页进到详情界面）")
    @PostMapping("/task/detail")
    R<TaskDetailRSP> taskDetail(@RequestBody @Valid TaskBaseREQ req);

    @ApiOperation("流程详情（列表页进到详情界面）")
    @PostMapping("/process/detail")
    R<ProcessDetailRSP> processDetail(@RequestBody @Valid ProcessBaseREQ req);

    @ApiOperation("随机退回-获取可退回节点")
    @PostMapping("/returnable/nodes")
    R<List<ExecutionReturnableNodesRSP>> returnableNodes(@RequestBody @Valid ExecutionReturnableNodesREQ req);

}
