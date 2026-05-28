package cn.zswltech.mithras.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * oa认证换取tocken
 * @author: jackerhe
 * @date: 2022/9/28 11:33 上午
 **/
@Data
@ApiModel("消息通知-读消息请求体")
public class OAAuthREQ {

    @ApiModelProperty(value = "secret")
    private String secret;

    @ApiModelProperty(value = "timestamp")
    private String timestamp;

    @ApiModelProperty(value = "random")
    private String random;

    @ApiModelProperty(value = "mithrasClientId")
    private Long mithrasClientId;

}
