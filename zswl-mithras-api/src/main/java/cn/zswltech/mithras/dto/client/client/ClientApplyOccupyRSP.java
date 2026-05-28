package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 生效
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("客户状态和管控权限-请求体")
public class ClientApplyOccupyRSP {

    @ApiModelProperty("客户是否被占有信息状态")
    public String message;

}
