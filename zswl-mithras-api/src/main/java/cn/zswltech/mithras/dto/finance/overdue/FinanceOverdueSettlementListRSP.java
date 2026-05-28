package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description 应收逾期结算表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期结算表列表-返回体")
public class FinanceOverdueSettlementListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
    * 收款明细id
    */
    @ApiModelProperty(value = "收款明细id")
    private Long collectionId;

    /**
    * 收款编号
    */
    @ApiModelProperty(value = "收款编号")
    private String collectionCode;

    /**
    * 单据状态
    */
    @ApiModelProperty(value = "单据状态 OverdueRecordStatueEnum")
    private String recordStatus;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 合同编号
    */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    /**
    * 项目名称
    */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
    * 客户id
    */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    /**
    * 客户名称
    */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
    * 单据日期
    */
    @ApiModelProperty(value = "单据日期")
    private LocalDate recordBillDate;

    /**
    * 结算日期
    */
    @ApiModelProperty(value = "结算日期")
    private LocalDate settlementDate;

    /**
    * 结算记录的凭证记账日期
    */
    @ApiModelProperty(value = "结算记录的凭证记账日期")
    private LocalDate voucherAccountDate;

    /**
    * 结算关系
    */
    @ApiModelProperty(value = "结算关系 OverdueSettlementRelationEnum")
    private String settlementRelation;

    /**
    * 结算金额（元）
    */
    @ApiModelProperty(value = "结算金额（元）")
    private BigDecimal settlementAmount;

    /**
    * 审批状态
    */
    @ApiModelProperty(value = "审批状态 ProjProcessState")
    private String approvalStatus;

}
