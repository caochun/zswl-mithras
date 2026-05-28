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
public class DefendantRemoveCommand {

    @ApiModelProperty(value = "诉讼登记id")
    private Long lrId;

    @ApiModelProperty(value = "主键列表")
    private Set<Long> ids;
}
