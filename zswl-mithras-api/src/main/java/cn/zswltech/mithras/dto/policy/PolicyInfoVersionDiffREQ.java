package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("版本差异比较-入参")
@Data
public class PolicyInfoVersionDiffREQ {
    @ApiModelProperty("版本id")
    @NotNull
    private Long id;
}
