package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/

@Data
public class MarginBaseInfoAddREQ {

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户id:项目主办id")
    private Long clientId;

    @ApiModelProperty("计划收取保证金")
    private Long planMarginAmount;

    @ApiModelProperty("计划收款日期")
    private LocalDate planMarginDate;

    @ApiModelProperty("计划收款保证金总额")
    private Long totalReceivableAmount;

}
