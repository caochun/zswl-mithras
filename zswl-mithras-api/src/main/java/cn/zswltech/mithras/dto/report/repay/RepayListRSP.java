package cn.zswltech.mithras.dto.report.repay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 征信报送-还款表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@ApiModel("征信报送-还款表返回值")
@Data
public class RepayListRSP {

    @ApiModelProperty("业务标识")
    private String businessKey;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * {@link cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum}
     */
    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty("idKey(结构：计划id_实际还款id)")
    private String idKey;

    @ApiModelProperty("借据编号")
    private String paymentApplyCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("应收日期")
    private LocalDate cashFlowDate;

    @ApiModelProperty("宽限期(天)")
    private Integer gracePeriod;

    @ApiModelProperty("应收租金")
    private Long rent;

    @ApiModelProperty("应收本金")
    private Long principal;

    @ApiModelProperty("收款日期")
    private LocalDate payDate;

    @ApiModelProperty("实收金额")
    private Long collectionAmount;

    @ApiModelProperty("实收本金")
    private Long collectionPrincipal;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("合同ID")
    private Long contractId;

    @ApiModelProperty("付款申请id")
    private Long paymentId;
}
