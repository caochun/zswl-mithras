package cn.zswltech.mithras.api.payment.register;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhouning
 * @date 2025/03/13
 * @description
 */
@Data
public class RegisterSaveRsp {

    @ApiModelProperty(value = "登记用户的身份")
    private String code;

    @ApiModelProperty(value = "登记类型")
    private String message;

    @ApiModelProperty(value = "填表人证件类型")
    private String time;

    @ApiModelProperty(value = "填表人证件号码")
    private String data;

}
