package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 转办
 *
 * @author wangchuanhao
 * @date 2022/7/28 3:00 PM
 */
@Data
public class ExecutionTransferREQ extends ExecutionProcessBaseREQ {

    @ApiModelProperty("被转办人id")
    @NotNull
    private Long employeeId;

    @ApiModelProperty("转办节点id")
//    @NotNull
    private String taskActivityId;

}
