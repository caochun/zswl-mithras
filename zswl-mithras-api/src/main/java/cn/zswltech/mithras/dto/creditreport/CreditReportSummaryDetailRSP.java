package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 征信报告-信息概要表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-信息概要表列表-返回体")
public class CreditReportSummaryDetailRSP {

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
    * 首次有信贷交易年份
    */
    @ApiModelProperty(value = "首次有信贷交易年份")
    private Integer firstCredityear;

    /**
    * 信贷交易机构数
    */
    @ApiModelProperty(value = "信贷交易机构数")
    private Integer creditOrganizationNumber;

    /**
    * 未结清信贷交易机构数
    */
    @ApiModelProperty(value = "未结清信贷交易机构数")
    private Integer unsettledCreditOrganizationNumber;

    /**
    * 首次有相关还款责任的年份
    */
    @ApiModelProperty(value = "首次有相关还款责任的年份")
    private Integer firstRepaymentResponsibilityYear;

    /**
    * 借贷交易-余额
    */
    @ApiModelProperty(value = "借贷交易-余额")
    private Double loanTransactionBalance;

    /**
    * 借贷交易-被追偿余额
    */
    @ApiModelProperty(value = "借贷交易-被追偿余额")
    private Double loanTransactionRecoveryBalance;

    /**
    * 借贷交易-关注类余额
    */
    @ApiModelProperty(value = "借贷交易-关注类余额")
    private Double loanTransactionFocusBalance;

    /**
    * 借贷交易-不良类余额
    */
    @ApiModelProperty(value = "借贷交易-不良类余额")
    private Double loanTransactionBadBalance;

    /**
    * 担保交易-余额
    */
    @ApiModelProperty(value = "担保交易-余额")
    private Double guaranteeTransactionBalance;

    /**
    * 担保交易-关注类余额
    */
    @ApiModelProperty(value = "担保交易-关注类余额")
    private Double guaranteeTransactionFocusBalance;

    /**
    * 担保交易-不良类余额
    */
    @ApiModelProperty(value = "担保交易-不良类余额")
    private Double guaranteeTransactionBadBalance;

    /**
    * 非信贷交易账户数
    */
    @ApiModelProperty(value = "非信贷交易账户数")
    private Integer nonCreditTransactionNumber;

    /**
    * 欠税记录条数
    */
    @ApiModelProperty(value = "欠税记录条数")
    private Integer taxArrearsRecordsNumber;

    /**
    * 民事判决记录条数
    */
    @ApiModelProperty(value = "民事判决记录条数")
    private Integer civilJudgmentRecordsNumber;

    /**
    * 强制执行记录条数
    */
    @ApiModelProperty(value = "强制执行记录条数")
    private Integer mandatoryExecutionRecordsNumber;

    /**
    * 行政处罚记录条数
    */
    @ApiModelProperty(value = "行政处罚记录条数")
    private Integer administrativePenaltyRecordsNumber;


}
