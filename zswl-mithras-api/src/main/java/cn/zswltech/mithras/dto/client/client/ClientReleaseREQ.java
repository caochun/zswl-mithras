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
@ApiModel("客户释放-请求体")
public class ClientReleaseREQ {

    @ApiModelProperty("客户id")
    public Long clientId;


}
