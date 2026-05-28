package cn.zswltech.mithras.dto.projestablish.pricefactoring;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("保理报价方案详情-请求体")
public class ProjEstablishFactoringPriceDetailREQ {

    @ApiModelProperty("id")
    private Long id;
}
