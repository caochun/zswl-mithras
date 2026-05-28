package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2024/4/3
 * @description
 */
@Data
public class TransferUserListREQ {
    @ApiModelProperty("流程类型")
    @NotBlank(message = "流程类型不能为空")
    private String processModelType;

    @ApiModelProperty("流程节点类型")
    @NotBlank(message = "流程节点类型不能为空")
    private String activityId;

    @ApiModelProperty("用户名称")
    private String userName;
}
