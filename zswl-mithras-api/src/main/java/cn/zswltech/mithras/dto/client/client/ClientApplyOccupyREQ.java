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
@ApiModel("客户是否被占有-请求体")
public class ClientApplyOccupyREQ {

    @ApiModelProperty("客户id")
    public Long clientId;

    @ApiModelProperty("流程实例id")
    public String processInstanceId;
}
