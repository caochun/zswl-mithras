package cn.zswltech.mithras.dto.projpricing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/12/24
 * @description
 */
@Data
public class ProjPricingCreateREQ {
    @NotNull(message = "项目评审id不能为空")
    @ApiModelProperty("项目评审ID")
    private Long projReviewId;
}
