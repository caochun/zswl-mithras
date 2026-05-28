package cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex;

import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


/**
 * LiquidityIndexDetailRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "流动性指标返回体")
public class LiquidityBoardDetailRSP {

    @ApiModelProperty(value = "数据时点")
    private LocalDate date;

    @ApiModelProperty(value = "期初余额")
    private LiquidityColorVo initialBalance;

    @ApiModelProperty(value = "预计回收租金")
    private LiquidityColorVo expectedRentRecovery;

    @ApiModelProperty(value = "现金流支出")
    private LiquidityColorVo cashFlowExpenditure;

    @ApiModelProperty(value = "债务偿还")
    private LiquidityColorVo debtRepayment;

    @ApiModelProperty(value = "非ABS还款")
    private LiquidityColorVo nonAbsRepayment;

    @ApiModelProperty(value = "ABS还款")
    private LiquidityColorVo absRepayment;

    @ApiModelProperty(value = "刚性支出")
    private LiquidityColorVo rigidExpenditure;

    @ApiModelProperty(value = "期末余额")
    private LiquidityColorVo endingBalance;

    // @ApiModelProperty(value = "监管户资金")
    @ApiModelProperty(value = "监管户资金余额")
    private LiquidityColorVo supervisedAccountFunds;

    @ApiModelProperty(value = "监管户净流入（累计）")
    private LiquidityColorVo supervisedAccountNetInflow;

    @ApiModelProperty(value = "监管户资金的负值")
    private LiquidityColorVo negativeSupervisedAccountFunds;

//    @ApiModelProperty(value = "非监管户资金")
    @ApiModelProperty(value = "非监管户资金余额")
    private LiquidityColorVo nonSupervisedAccountFunds;

    @ApiModelProperty(value = "非监管户净流入（累计）")
    private LiquidityColorVo nonSupervisedAccountNetInflow;

    // 新增两个字段：监管户净流入（当日）、非监管户净流入（当日）
    /**
     * 日期=当日，账户性质=监管户
     * 提款+租金回流+其它流入-投放(还本付息+还本付息(调整值))-刚性支出-其它支出
     */
    @ApiModelProperty(value = "监管户净流入（当日）")
    private LiquidityColorVo dailySupervisedAccountIncome;

    /**
     * 日期=当日，账户性质=非监管
     * 提款+租金回流+其它流入-投放(还本付息+还本付息(调整值))-刚性支出-其它支出
     */
    @ApiModelProperty(value = "非监管户净流入（当日）")
    private LiquidityColorVo dailyNonSupervisedAccountIncome;

//    @ApiModelProperty(value = "流动性缺口（当天）")
//    private LiquidityColorVo dailyLiquidityGap;

//    @ApiModelProperty(value = "流动性覆盖率（30天）")
//    private LiquidityColorVo thirtyDayLiquidityCoverageRatio;

    @ApiModelProperty(value = "期间实际或计划投放等支出金额")
    private LiquidityColorVo periodActualOrPlannedExpenditure;

    @ApiModelProperty(value = "期间实际或计划融资等收款金额")
    private LiquidityColorVo periodActualOrPlannedFinancingReceipts;

    @ApiModelProperty(value = "当日最大可用余额")
    private LiquidityColorVo dailyMaxAvailableBalance;
}
