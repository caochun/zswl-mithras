package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获取可退回节点-返回
 */
@Data
public class ExecutionReturnableNodesRSP {

    @ApiModelProperty("任务节点名称")
    private String taskNodeName;

    @ApiModelProperty("任务节点id")
    private String taskActivityId;

}
