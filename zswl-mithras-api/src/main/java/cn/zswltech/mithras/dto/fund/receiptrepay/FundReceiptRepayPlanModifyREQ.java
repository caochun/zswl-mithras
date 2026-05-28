package cn.zswltech.mithras.dto.fund.receiptrepay;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 还款计划
 * @author zhaozhengkang
 * @date 2023-02-20
 */
@Data
@ApiModel("还款计划编辑-请求体")
public class FundReceiptRepayPlanModifyREQ {

    /**
    * 主键
    */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
    * 收付款id
    */
    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;

    /**
    * batch_id
    */
    @ApiModelProperty(value = "batch_id")
    private Long batchId;

    /**
    * 融资id
    */
    @ApiModelProperty(value = "融资id")
    private Long financingId;

    /**
    * 融资数据版本
    */
    @ApiModelProperty(value = "融资数据版本")
    private String financingVersion;

    /**
    * 融资机构
    */
    @ApiModelProperty(value = "融资机构")
    private String financingOrg;

    /**
    * 融资金额（元）
    */
    @ApiModelProperty(value = "融资金额（元）")
    private Long financingAmount;

    /**
    * 累计已还本金（元）
    */
    @ApiModelProperty(value = "累计已还本金（元）")
    private Long paidPrincipal;

    /**
    * 累计已还利息（元）
    */
    @ApiModelProperty(value = "累计已还利息（元）")
    private Long paidInterest;

    /**
    * 配套项目
    */
    @ApiModelProperty(value = "配套项目")
    private String supportingProject;

    /**
    * 质押合同编号
    */
    @ApiModelProperty(value = "质押合同编号")
    private String pledgeContracCode;

    /**
    * 借款日期
    */
    @ApiModelProperty(value = "借款日期")
    private LocalDateTime borrowingDate;

    /**
    * 到期日期
    */
    @ApiModelProperty(value = "到期日期")
    private LocalDateTime expirationDate;

    /**
    * 本月计划还款合计（元）
    */
    @ApiModelProperty(value = "本月计划还款合计（元）")
    private Long planedRepayAmount;

    /**
    * 本月计划还款本金（元）
    */
    @ApiModelProperty(value = "本月计划还款本金（元）")
    private Long planedRepayPrincipal;

    /**
    * 本月计划还款利息（元）
    */
    @ApiModelProperty(value = "本月计划还款利息（元）")
    private Long planedRepayInterest;

    /**
    * 计划还本日
    */
    @ApiModelProperty(value = "计划还本日")
    private LocalDateTime planedRepayPrincipleDate;

    /**
    * 计划还息日
    */
    @ApiModelProperty(value = "计划还息日")
    private LocalDateTime planedRepayInterestDate;

}
