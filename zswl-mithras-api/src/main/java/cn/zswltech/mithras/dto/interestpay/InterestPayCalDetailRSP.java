package cn.zswltech.mithras.dto.interestpay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestPayCalDetailRSP extends PageReq {

    @ApiModelProperty("主键id")
    private long id;

    @ApiModelProperty("融资id")
    private Long financingId;


    @ApiModelProperty("产品ID")
    private Long financingProductId;

    /**
     * 证券简称
     */
    @ApiModelProperty("证券简称")
    private String abbreviation;

    @ApiModelProperty("计息日期")
    private LocalDate interestDate;

    @ApiModelProperty("融资余额(元)")
    private Long remainingAmount;

    @ApiModelProperty("还款本金")
    private Long principleAmount;

    @ApiModelProperty("还款利息")
    private Long interestAmount;

    @ApiModelProperty("融资利率")
    private Integer financingRate;

    @ApiModelProperty("日利率")
    private Integer dailyRate;

    @ApiModelProperty("当日应付利息")
    private Long dailyAmount;

    @ApiModelProperty("当年累计应付利息")
    private Long yearCapitalCost;

    @ApiModelProperty("当年累计应付利息（税后）")
    private Long yearCapitalCostAfterTax;

    @ApiModelProperty("是否确认")
    private Integer isConfirmed;

    @ApiModelProperty("钆差金额")
    private Long financingCostDiff;

    @ApiModelProperty("期初计提利息余额")
    private Long beginOfPeriodInterestBalance;

    @ApiModelProperty("期末计提利息余额")
    private Long endOfPeriodInterestBalance;
}
