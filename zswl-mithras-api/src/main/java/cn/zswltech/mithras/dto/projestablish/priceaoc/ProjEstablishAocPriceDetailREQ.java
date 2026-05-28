package cn.zswltech.mithras.dto.projestablish.priceaoc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("债券转让报价方案详情-请求体")
public class ProjEstablishAocPriceDetailREQ {

    @ApiModelProperty("id")
    private Long id;
}
