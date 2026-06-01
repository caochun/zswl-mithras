package cn.zswltech.mithras.service.overdue.application.command;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 11:41
 */
@Data
public class DefendantAddCommand {

    @ApiModelProperty(value = "诉讼登记id")
    private Long lrId;

    @ApiModelProperty(value = "被告名称")
    private String name;
    @ApiModelProperty(value = "合同地位")
    private String role;
    @ApiModelProperty(value = "证件类型")
    private String certificateType;
    @ApiModelProperty(value = "证件号码")
    private String certificateNumber;
}
