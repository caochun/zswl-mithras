package cn.zswltech.mithras.dto.projpricing.cashflowplan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/1/2
 * @description
 */
@Data
@ApiModel("IRR测算结果-返回体")
public class ProjPricingIRRCalculateResultRSP {
    @ApiModelProperty("irr")
    private String irr;

    @ApiModelProperty("测算结果详情文件id")
    private Long fileId;
}
