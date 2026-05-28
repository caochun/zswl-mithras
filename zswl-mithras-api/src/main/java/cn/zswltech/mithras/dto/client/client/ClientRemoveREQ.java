package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("客户删除-请求体")
public class ClientRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    public Long id;
}
