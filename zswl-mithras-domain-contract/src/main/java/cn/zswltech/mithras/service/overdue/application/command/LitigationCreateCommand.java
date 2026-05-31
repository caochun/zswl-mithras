package cn.zswltech.mithras.service.overdue.application.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 10:51
 */
@Data
public class LitigationCreateCommand {

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;
}
