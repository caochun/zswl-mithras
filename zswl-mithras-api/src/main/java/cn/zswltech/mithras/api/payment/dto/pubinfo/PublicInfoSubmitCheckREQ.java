package cn.zswltech.mithras.api.payment.dto.pubinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author bigbear
 * @date 2024/9/10 16:43
 * @description
 */
@Data
public class PublicInfoSubmitCheckREQ {

    @ApiModelProperty(value = "流程ID")
    @NotBlank(message = "流程ID不能为空")
    private String processInstanceId;

}
