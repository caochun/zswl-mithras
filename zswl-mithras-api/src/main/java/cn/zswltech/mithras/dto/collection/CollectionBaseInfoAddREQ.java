package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/

@Data
public class CollectionBaseInfoAddREQ {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("付款code")
    private String paymentCode;

    @ApiModelProperty("借据id")
    private Long receiptId;

    @ApiModelProperty("借据code")
    private String receiptCode;

    @ApiModelProperty("期项")
    private Integer phase;


    @ApiModelProperty("合同编号")
    private String contractCode;


    @ApiModelProperty("本金")
    private Long principal;


    @ApiModelProperty("利息")
    private Long interest;


    @ApiModelProperty("计划收款金额")
    private Long planCollectionAmount;


    @ApiModelProperty("计划收款日期")
    private LocalDate planCollectionDate;


    @ApiModelProperty("现金流项目")
    private String cashFlowItem;


    @ApiModelProperty("现金流金额")
    private Long cashFlowAmount;

    @ApiModelProperty("现金流编号")
    private String collectionCode;


}
