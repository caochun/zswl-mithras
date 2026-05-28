package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/
@Data
@ApiModel("收款核销详情-返回体")
public class MarginCashInfoRSP {

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("收款核销id")
    private Long collectionId;

    @ApiModelProperty("计划收款金额")
    private Long planCollectionAmount;

    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    @ApiModelProperty("计划收款日")
    private LocalDate planCollectionDate;

    @ApiModelProperty("现金流金额")
    private Long cashFlowAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;

    @ApiModelProperty("剩余可抵扣金额")
    private Long lastDeductAmount;

    @ApiModelProperty("剩余可抵扣本金")
    private Long lastDeductPrincipal;

    @ApiModelProperty("剩余可抵扣利息")
    private Long lastDeductInterest;

    @ApiModelProperty("剩余可抵扣罚息")
    private Long lastDeductPenaltyInterest;

}
