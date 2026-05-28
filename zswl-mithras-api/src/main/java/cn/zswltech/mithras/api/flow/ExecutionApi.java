package cn.zswltech.mithras.api.flow;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.flow.execution.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 流程 操作
 *
 * @author wangchuanhao
 * @date 2022/6/22 10:08 PM
 */
@Api(tags = "流程相关-操作-接口")
@RequestMapping("/flow/execution")
public interface ExecutionApi {

    @ApiOperation("一键批量通过")
    @PostMapping("/batch/pass")
    R<Void> batchPass(@RequestBody @Valid ExecutionBatchPassREQ req);

    @ApiOperation("通过")
    @PostMapping("/pass")
    R<Void> pass(@RequestBody @Valid ExecutionPassREQ req);

    @ApiOperation("拒绝")
    @PostMapping("/reject")
    R<Void> reject(@RequestBody @Valid ExecutionTaskBaseREQ req);

    @ApiOperation("退回到发起人")
    @PostMapping("/backToStartUser")
    R<Void> backToStartUser(@RequestBody @Valid ExecutionBackToStartUserREQ req);

    @ApiOperation("退回到指定节点")
    @PostMapping("/backToStep")
    R<Void> backToStep(@RequestBody @Valid ExecutionBackToStepREQ req);

    @ApiOperation("协同")
    @PostMapping("/collaborate")
    R<Void> collaborate(@RequestBody @Valid ExecutionCollaborateREQ req);

    @ApiOperation("抄送")
    @PostMapping("/cc")
    R<Void> cc(@RequestBody @Valid ExecutionProcessBaseREQ req);

    @ApiOperation("取消流程")
    @PostMapping("/cancelProcess")
    R<Void> cancelProcess(@RequestBody @Valid ExecutionProcessBaseREQ req);

    @ApiOperation("撤回到发起人")
    @PostMapping("/withdrawToStartUser")
    R<Void> withdrawToStartUser(@RequestBody @Valid ExecutionProcessBaseREQ req);

    @ApiOperation("撤回任务")
    @PostMapping("/withdrawTask")
    R<Void> withdrawTask(@RequestBody @Valid ExecutionTaskBaseREQ req);

    @ApiOperation("转办")
    @PostMapping("/transfer")
    R<Void> transfer(@RequestBody @Valid ExecutionTransferREQ req);

    @ApiOperation("跳转")
    @PostMapping("/jump")
    R<Void> jump(@RequestBody @Valid ExecutionJumpREQ req);

    @ApiOperation("一键通过")
    @PostMapping("/passAll")
    R<Void> passAll(@RequestBody @Valid ExecutionProcessBaseREQ req);

    @ApiOperation("一键拒绝")
    @PostMapping("/rejectAll")
    R<Void> rejectAll(@RequestBody @Valid ExecutionProcessBaseREQ req);

    @ApiOperation("随机退回")
    @PostMapping("/randomReturn")
    R<Void> randomReturn(@RequestBody @Valid ExecutionRandomReturnREQ req);

}
