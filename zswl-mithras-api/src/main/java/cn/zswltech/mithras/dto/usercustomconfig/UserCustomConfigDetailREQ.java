package cn.zswltech.mithras.dto.usercustomconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/2/11
 * @description
 */
@Data
public class UserCustomConfigDetailREQ {
    @ApiModelProperty(value = "配置key")
    private String configKey;
}
