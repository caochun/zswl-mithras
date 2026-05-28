package cn.zswltech.mithras.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
public class DashboardFundFinanceStatisticsRSP {
    @JsonIgnore
    private Integer sort;
    @ApiModelProperty("分组code")
    private String groupCode;
    @ApiModelProperty("分组名称")
    private String group;
    @ApiModelProperty("本月应还")
    private ValueUnitDTO repayTotalAmount;
    @ApiModelProperty("本月未还")
    private ValueUnitDTO repayBalanceAmount;
    @ApiModelProperty("3天内到期")
    private ValueUnitDTO repayInThreeDays;
    @ApiModelProperty("7天内到期")
    private ValueUnitDTO repayInSevenDays;
    @ApiModelProperty("合计数量")
    private Integer quantity;
    @ApiModelProperty("融资金额/授信金额")
    //剩余本金
    private ValueUnitDTO totalAmount;
    @ApiModelProperty("剩余金额")
    //剩余利息
    private ValueUnitDTO balanceAmount;
    @ApiModelProperty("已用金额")
    private ValueUnitDTO usedAmount;
    @ApiModelProperty("融资加权合同利率")
    private ValueUnitDTO averageInterestRate;
    @ApiModelProperty("久期")
    private ValueUnitDTO duration;
    @ApiModelProperty("融资加权综合成本")
    private ValueUnitDTO averageCostFunds;
//    @ApiModelProperty("融资加权综合成本（本年新增）")
//    private ValueUnitDTO yearFundsCost;
//    @ApiModelProperty("综合资金成本（本月新增）")
//    private ValueUnitDTO monthFundsCost;
    @ApiModelProperty("直接融资加权成本")
    private ValueUnitDTO costFundsZR;
    @ApiModelProperty("间接融资加权成本")
    private ValueUnitDTO costFundsJR;
    @ApiModelProperty("直接融资加权成本-本年新增")
    private ValueUnitDTO costFundsZRThisYear;
    @ApiModelProperty("间接融资加权成本-本年新增")
    private ValueUnitDTO costFundsJRThisYear;
}
