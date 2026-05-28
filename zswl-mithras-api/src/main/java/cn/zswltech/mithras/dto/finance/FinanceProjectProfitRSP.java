package cn.zswltech.mithras.dto.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Data
@ApiModel("财务管理-项目利润-分页列表-返回参数")
public class FinanceProjectProfitRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("年份")
    private Integer year;

    @ApiModelProperty("月份")
    private Integer month;

    @ApiModelProperty("当年累计收入")
    private Long totalIncomeThisYear;

    @ApiModelProperty("当年累计资金成本")
    private Long totalCostThisYear;

    @ApiModelProperty("当年累计风险金")
    private Long totalRiskThisYear;

    @ApiModelProperty("当年累计附加税")
    private Long totalAdditionalTaxThisYear;

    @ApiModelProperty("当年累计印花税")
    private Long totalStampTaxThisYear;

    @ApiModelProperty("当年累计利润总额")
    private Long totalProfitThisYear;

    @ApiModelProperty("当月收入")
    private Long incomeThisMonth;

    @ApiModelProperty("当月资金成本")
    private Long costThisMonth;

    @ApiModelProperty("当月风险金")
    private Long riskThisMonth;

    @ApiModelProperty("当月利润")
    private Long profitThisMonth;

    @ApiModelProperty("是否已经确认")
    private Integer isConfirmed;
}
