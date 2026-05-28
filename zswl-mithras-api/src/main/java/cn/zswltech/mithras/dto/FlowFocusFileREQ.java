package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("审批流-重点关注文件-请求体")
public class FlowFocusFileREQ extends VersionBaseREQ {
    @ApiModelProperty("流程类型")
    @NotBlank(message = "流程类型不能为空")
    private String processModuleType;

    @ApiModelProperty("业务key")
    @NotBlank(message = "业务key不能为空")
    private String businessKey;
}
