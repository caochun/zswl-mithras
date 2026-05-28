package cn.zswltech.mithras.dto.afterlease.rentcollection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-11-17
 **/

@Data
@ApiModel("租金信息-请求体")
public class RentDetailInfoREQ {

    @NotNull
    @ApiModelProperty(value = "收款核销id")
    private Long id;
}
