package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @description 征信报告-相关还款责任信息概要表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-相关还款责任信息概要表编辑-请求体")
public class CreditReportRepaymentResponsibilityModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 查询编号
    */
    @ApiModelProperty(value = "查询编号")
    private Long creditCode;

    /**
    * 征信报告基本表id
    */
    @ApiModelProperty(value = "征信报告基本表id")
    private Long creditReportId;

    /**
    * 责任类型
    */
    @ApiModelProperty(value = "责任类型")
    private String responsibilityType;

    /**
    * 被追偿业务-还款责任金额
    */
    @ApiModelProperty(value = "被追偿业务-还款责任金额")
    private BigDecimal recoverableRepaymentResponsibilityAmount;

    /**
    * 被追偿业务-账户数
    */
    @ApiModelProperty(value = "被追偿业务-账户数")
    private BigDecimal recoverableAccountNumber;

    /**
    * 被追偿业务-余额
    */
    @ApiModelProperty(value = "被追偿业务-余额")
    private BigDecimal recoverableBalance;

    /**
    * 其他借贷交易-还款责任金额
    */
    @ApiModelProperty(value = "其他借贷交易-还款责任金额")
    private BigDecimal otherRepaymentResponsibilityAmount;

    /**
    * 其他借贷交易-账户数
    */
    @ApiModelProperty(value = "其他借贷交易-账户数")
    private BigDecimal otherAccountNumber;

    /**
    * 其他借贷交易-余额
    */
    @ApiModelProperty(value = "其他借贷交易-余额")
    private BigDecimal otherBalance;

    /**
    * 其他借贷交易-关注类余额
    */
    @ApiModelProperty(value = "其他借贷交易-关注类余额")
    private BigDecimal otherFocusBalance;

    /**
    * 其他借贷交易-不良类余额
    */
    @ApiModelProperty(value = "其他借贷交易-不良类余额")
    private BigDecimal otherBadBalance;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除，1-已删除")
    private Integer deleted;

}
