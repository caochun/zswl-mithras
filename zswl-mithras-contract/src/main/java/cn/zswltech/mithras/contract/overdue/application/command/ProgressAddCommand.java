package cn.zswltech.mithras.contract.overdue.application.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 11:44
 */
@Data
public class ProgressAddCommand {

    @ApiModelProperty(value = "诉讼登记id")
    private Long lrId;
    @ApiModelProperty(value = "阶段")
    private String stage;
    @ApiModelProperty(value = "状态")
    private String status;
}
