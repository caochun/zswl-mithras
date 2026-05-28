package cn.zswltech.mithras.dto.usercustomconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2025/2/11
 * @description
 */
@Data
public class UserCustomConfigSaveREQ {
    @ApiModelProperty(value = "配置key")
    @NotBlank(message = "配置key不能为空")
    private String configKey;

    @ApiModelProperty(value = "配置value")
    @NotBlank(message = "配置value不能为空")
    private String configValue;

    @ApiModelProperty(value = "元数据类型")
    private String metadataType;
}
