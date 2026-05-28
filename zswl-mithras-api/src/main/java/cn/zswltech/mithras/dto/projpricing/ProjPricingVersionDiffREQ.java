package cn.zswltech.mithras.dto.projpricing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@ApiModel("版本差异比较-入参")
@Data
public class ProjPricingVersionDiffREQ {
    @ApiModelProperty("版本id")
    @NotNull
    private Long id;
}
