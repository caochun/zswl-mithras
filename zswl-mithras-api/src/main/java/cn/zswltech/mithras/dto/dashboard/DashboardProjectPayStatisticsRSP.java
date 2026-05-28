package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/23
 * @description
 */
@Data
public class DashboardProjectPayStatisticsRSP {
    @ApiModelProperty("投放合同数量")
    private Integer payContractQuantity;

    @ApiModelProperty("投放金额")
    private ValueUnitDTO payAmount;

    @ApiModelProperty("加权IRR")
    private ValueUnitDTO averageIrr;

    @ApiModelProperty("加权手续费率")
    private ValueUnitDTO averageCommissionRate;

    @ApiModelProperty("加权合同利率")
    private ValueUnitDTO averageContractInterestRate;
}
