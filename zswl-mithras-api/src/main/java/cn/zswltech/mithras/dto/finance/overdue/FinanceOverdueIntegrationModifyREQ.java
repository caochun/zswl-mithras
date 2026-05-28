package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * @description 应收逾期集成表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期集成表编辑-请求体")
public class FinanceOverdueIntegrationModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
    * 国有类型
    */
    @ApiModelProperty(value = "国有类型")
    private String ownedType;

    /**
    * 实控人
    */
    @ApiModelProperty(value = "实控人")
    private String actualController;

    /**
    * 款项内容.款项内容编码
    */
    @ApiModelProperty(value = "款项内容.款项内容编码")
    private String paymentNumber;

    /**
    * 单据账龄起算日
    */
    @ApiModelProperty(value = "单据账龄起算日")
    private LocalDate recordStartDate;

    /**
    * 单据日期
    */
    @ApiModelProperty(value = "单据日期")
    private LocalDate recordBillDate;

    /**
    * 科目
    */
    @ApiModelProperty(value = "科目")
    private String accounttypeNumber;

    /**
    * 约定收款日期
    */
    @ApiModelProperty(value = "约定收款日期")
    private LocalDate recordDueDate;

    /**
    * 约定收款条件
    */
    @ApiModelProperty(value = "约定收款条件")
    private String recordPaymentTerms;

    /**
    * 应收金额（元）
    */
    @ApiModelProperty(value = "应收金额（元）")
    private BigDecimal receAmount;

    /**
    * 行业正常收款周期
    */
    @ApiModelProperty(value = "行业正常收款周期")
    private Long collectionCycle;

}
