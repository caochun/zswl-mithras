package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 随机退回
 */
@Data
public class ExecutionRandomReturnREQ extends ExecutionProcessBaseREQ {

    @ApiModelProperty("随机退回至哪个节点id")
    private String activityId;

    /**
     * 任务id
     */
    @NotBlank(message = "任务id不能为空")
    private String taskId;

    @ApiModelProperty("驳回类型：1逐级审批，2直达本节点")
    private Integer backType;

}
