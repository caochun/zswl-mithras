package cn.zswltech.mithras.dto.datacompare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("保理报价方案详情-请求体")
public class ProjReviewFactoringPriceDetailREQ {

    @ApiModelProperty("id")
    private Long id;
}
