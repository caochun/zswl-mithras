package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-16
 **/
@Data
public class MarginDeductDetailRSP {

    @ApiModelProperty("抵扣期项")
    private Integer phase;

    @ApiModelProperty("收款现金流编号")
    private String paymentCode;

    @ApiModelProperty("现金流信息")
    private MarginCashInfoRSP cashInfo;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("日期")
    private LocalDate collectionDate;

    @ApiModelProperty("抵扣本金")
    private Long deductPrincipal;

    @ApiModelProperty("抵扣利息")
    private Long deductInterest;

    @ApiModelProperty("抵扣罚息")
    private Long deductPenaltyInterest;

    @ApiModelProperty("抵扣租金")
    private Long deductRent;
}
