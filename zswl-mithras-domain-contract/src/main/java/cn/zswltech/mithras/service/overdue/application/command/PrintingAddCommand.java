package cn.zswltech.mithras.service.overdue.application.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:46
 */
@Data
@ApiModel(value = "文书用印新增请求")
public class PrintingAddCommand {

    @ApiModelProperty(value = "文书用印类型")
    @NotBlank(message = "文书用印类型不能为空")
    private String type;

    @ApiModelProperty(value = "文书用印原因")
    private String reason;
}
