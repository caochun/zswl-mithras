package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 合同IRR响应
 * @author: huangping
 * @date: 2026/1/27  16:37
 * @version: 1.0
 */
@Data
@ApiModel(value = "合同IRR响应")
public class ContractIrrRSP {

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "定价irr")
    private Integer pricingIrr;

    @ApiModelProperty(value = "最新审批通过的付款申请最低irr")
    private Integer payLowIrr;

    @ApiModelProperty(value = "合同最低irr")
    private Integer lowestIrr;

    @ApiModelProperty(value = "加权平均irr")
    private Integer averageIrr;


}
