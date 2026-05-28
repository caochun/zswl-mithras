package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 生效
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("客户权限生效-请求体")
public class ClientAuthorityEffectREQ {

    @NotNull
    @ApiModelProperty("id")
    public Long id;

    @ApiModelProperty("opinion")
    public String opinion;
}
