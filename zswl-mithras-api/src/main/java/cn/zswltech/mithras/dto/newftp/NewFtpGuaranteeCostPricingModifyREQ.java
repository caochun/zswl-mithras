package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 担保成本定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("担保成本定价编辑-请求体")
public class NewFtpGuaranteeCostPricingModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 当期均值
    */
    @ApiModelProperty(value = "当期均值")
    private Integer currentAverage;

}
