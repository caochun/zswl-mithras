package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("记录新增-请求体")
public class MarginRecordDeductAddREQ {
    @ApiModelProperty("保证金id")
    private Long marginId;

    @ApiModelProperty("收款类型")
    private String collectionType;

    @ApiModelProperty("抵扣日期")
    private LocalDate collectionDate;

    @ApiModelProperty("抵扣本金")
    private Long deductPrincipal;

    @ApiModelProperty("抵扣利息")
    private Long deductInterest;

    @ApiModelProperty("抵扣罚息")
    private Long deductPenaltyInterest;

    @ApiModelProperty("抵扣租金")
    private Long deductRent;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("抵扣期项")
    private Integer deductTerm;

    @ApiModelProperty("收款核销id")
    private Long collectionId;
}
