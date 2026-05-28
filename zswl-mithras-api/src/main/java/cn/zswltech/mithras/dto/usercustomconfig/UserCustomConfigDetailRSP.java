package cn.zswltech.mithras.dto.usercustomconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/2/11
 * @description
 */
@Data
public class UserCustomConfigDetailRSP {
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "配置key")
    private String configKey;

    @ApiModelProperty(value = "配置value")
    private String configValue;

    @ApiModelProperty(value = "元数据类型")
    private String metadataType;
}
