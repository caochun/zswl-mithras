package cn.zswltech.mithras.dto.projpricing.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/11
 * @description
 */
@Data
@ApiModel("项目评审-保存IRR-请求体")
public class ProjPricingIRRSaveREQ {
    @ApiModelProperty("项目定价id")
    @NotNull(message = "不能为空")
    private Long projPricingId;

    @ApiModelProperty("irr")
    @NotNull(message = "irr不能为空")
    private Integer irrPercent;
}
