package cn.zswltech.mithras.dto.projestablish.pricelease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("租赁报价方案详情-请求体")
public class ProjEstablishLeasePriceDetailREQ {

    @ApiModelProperty("id")
    private Long id;
}
