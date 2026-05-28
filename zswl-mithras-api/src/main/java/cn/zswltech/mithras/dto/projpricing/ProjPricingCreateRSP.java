package cn.zswltech.mithras.dto.projpricing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/12/24
 * @description
 */
@Data
public class ProjPricingCreateRSP {
    @ApiModelProperty("项目定价ID")
    private Long projPricingId;
}
