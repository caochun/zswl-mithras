package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("客户基本信息同步-请求体")
public class ClientSyncREQ {

    @NotNull
    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;

}
