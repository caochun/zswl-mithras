package cn.zswltech.mithras.dto.finance.accountage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description 帐龄-详情表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄-详情表新增-请求体")
public class FinanceAccountAgeItemAddREQ {

    /**
    * 帐龄id
    */
    @ApiModelProperty(value = "帐龄id")
    private Long accountAgeId;

    /**
    * 核算组织编码 默认 10000396
    */
    @ApiModelProperty(value = "核算组织编码 默认 10000396")
    private String accountancyOrganizationNumber;

    /**
    * 核算组织名称 默认 浙江浙商融资租赁有限公司
    */
    @ApiModelProperty(value = "核算组织名称 默认 浙江浙商融资租赁有限公司")
    private String accountancyOrganizationName;

    /**
    * 期初款项原值
    */
    @ApiModelProperty(value = "期初款项原值")
    private BigDecimal originalValueInitial;

    /**
    * 本期增加额 >0增加 <0减少
    */
    @ApiModelProperty(value = "本期增加额 >0增加 <0减少")
    private BigDecimal originalValueIncrease;

    @ApiModelProperty(value = "本期减少额 ")
    private BigDecimal originalValueReduce;

    /**
    * 期末款项原值
    */
    @ApiModelProperty(value = "期末款项原值")
    private BigDecimal originalValueFinal;

    /**
    * 币别
    */
    @ApiModelProperty(value = "币别")
    private String currency;

    /**
    * 科目名称编号
    */
    @ApiModelProperty(value = "科目名称编号")
    private String accountNumber;

    /**
    * 款项内容
    */
    @ApiModelProperty(value = "款项内容")
    private String paymentContent;

    /**
    * 客户id
    */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    /**
    * 客户单位名称， 取合同对应承租人的“客户名称”字段
    */
    @ApiModelProperty(value = "客户单位名称， 取合同对应承租人的“客户名称”字段")
    private String customerUnitName;

    /**
    * 业务日期
    */
    @ApiModelProperty(value = "业务日期")
    private LocalDate businessDate;

    /**
    * 账龄截止日
    */
    @ApiModelProperty(value = "账龄截止日")
    private LocalDate agingDeadline;

    /**
    * 业务账龄（月）向下取整
    */
    @ApiModelProperty(value = "业务账龄（月）向下取整")
    private Integer businessAge;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 收款id
    */
    @ApiModelProperty(value = "收款id")
    private Long collectionId;

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
    * 租金的应收日期 合同逾期日期
    */
    @ApiModelProperty(value = "租金的应收日期 合同逾期日期")
    private LocalDate planCollectionDate;

}
