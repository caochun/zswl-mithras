package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
@Data
public class DashboardProjectFinanceStatisticsRSP {
    @ApiModelProperty("分组code")
    private String groupCode;
    @ApiModelProperty("分组名称")
    private String group;
    @ApiModelProperty("合计数/未结清合同数")
    private Integer quantity;
    @ApiModelProperty("本月应收租金")
    private ValueUnitDTO planRentAmountThisMonth;
    @ApiModelProperty("本月未收租金")
    private ValueUnitDTO uncollectionRentAmountThisMonth;
    @ApiModelProperty("剩余敞口")
    private ValueUnitDTO totalExposure;
    @ApiModelProperty("存在质押合同数")
    private Integer pledgeQuantity;
    @ApiModelProperty("存在监管合同数")
    private Integer superviseQuantity;
    @ApiModelProperty("剩余租金总额")
    private ValueUnitDTO rentBalanceAmount;
    @ApiModelProperty("拨备金额")
    private ValueUnitDTO provisionBalanceAmount;
    @ApiModelProperty("金额")
    private ValueUnitDTO amount;
}
