package cn.zswltech.mithras.dto.client.clientversion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 客户版本差异比较
 *
 * @author wangchuanhao
 * @date 2022/6/30 9:27 PM
 */
@ApiModel("客户版本差异比较-入参")
@Data
public class ClientVersionDiffREQ {

    @ApiModelProperty("版本id")
    @NotNull
    private Long id;

    @ApiModelProperty("客户id")
    private Long clientId;

}
