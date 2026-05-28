package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-08-15
 **/

@Data
public class CollectionBaseInfoUpdateREQ {

    @NotNull
    @ApiModelProperty("收款核销id")
    private Long id;

    @ApiModelProperty("罚息金额")
    private Long penaltyInterestAmount;

    @ApiModelProperty("备注")
    private String comment;

}
