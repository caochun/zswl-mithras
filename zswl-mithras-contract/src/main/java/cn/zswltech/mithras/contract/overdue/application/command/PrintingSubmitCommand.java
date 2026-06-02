package cn.zswltech.mithras.contract.overdue.application.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/8 10:40
 */
@Data
@ApiModel(value = "用印申请提交命令")
public class PrintingSubmitCommand {
    @ApiModelProperty(value = "主键id")
    private Long id;
}
