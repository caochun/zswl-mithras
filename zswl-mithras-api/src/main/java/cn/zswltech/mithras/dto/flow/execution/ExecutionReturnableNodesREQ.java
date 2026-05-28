package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取可退回节点
 */
@Data
public class ExecutionReturnableNodesREQ {
    @ApiModelProperty("流程实例id")
    @NotBlank
    private String processInstanceId;

    @ApiModelProperty("任务id")
    @NotBlank
    private String taskId;

}
