package cn.zswltech.mithras.contract.overdue.application.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:58
 */
@Data
@ApiModel(value = "催收流程提交命令")
public class CollectionActionSubmitCommand {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "ocId")
    private Long ocId;
}
