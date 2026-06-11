package cn.zswltech.mithras.dto.gungnir;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author wanghui
 * @description 三方登录
 */
@Data
@ApiModel(value = "gungnir-请求体")
public class GungnirLoginREQ implements Serializable {

    @ApiModelProperty("登录账号")
    @NotBlank(message = "登录账号不得为空")
    public String account;


    @ApiModelProperty("登录密码")
    @NotBlank(message = "登录密码不得为空")
    private String password ;


}
