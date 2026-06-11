package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 16:51
 */
@ApiModel("付款详情-出参")
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentDetailRsp extends ListBaseRSP {
    /**
     * -----以下信息合同带入-----
     **/
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("项目code")
    private String projCode;
    @ApiModelProperty("项目批复金额")
    private Long approvedAmount;
    @ApiModelProperty("租赁类型(类别)")
    private String leaseType;
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private String leaseTypeCode;
    @ApiModelProperty(value = "业务类型")
    private String bizTypeCode;
    @ApiModelProperty("保理类型")
    private String factoringType;
    @ApiModelProperty("债权转让类型")
    private String zrType;
    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名")
    private String projSponsorUserName;
    @ApiModelProperty("业务部门")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名")
    private String bizDeptName;
    @ApiModelProperty("保证金")
    private Long contractEarnestMoney;
    @ApiModelProperty("名义货价")
    private Long contractNominalPrice;
    @ApiModelProperty("服务费/咨询费")
    private Long contractConsultingFee;
    @ApiModelProperty("手续费")
    private Long contractCommission;
    @ApiModelProperty(value = "手续费(元)")
    private Long commission;
    @ApiModelProperty(value = "首期利息(元)")
    private Long firstInstallmentInterest;
    @ApiModelProperty("合同金额")
    private Long applyCreditAmount;
    @ApiModelProperty("首期租金")
    private Long contractDownPayment;
    @ApiModelProperty("现金流项目")
    private String payables;
    @ApiModelProperty("计划付款金额(合同带入的报价)")
    private Long planedPaidAmount;
    @ApiModelProperty("计划付款日期(合同带入的合同生效时间)")
    private LocalDate planedPaidDate;
    @ApiModelProperty("项目评审id")
    private Long projReviewId;
    @ApiModelProperty("风控经理")
    private Long riskControlManagerId;
    @ApiModelProperty("合同概算IRR")
    private Integer contractEstimateIrr;
    /**
     * -----以下信息自动计算-----
     **/
    @ApiModelProperty("已付金额")
    private Long amountPaid;
    @ApiModelProperty("已申请金额")
    private Long amountApplied;
    @ApiModelProperty("剩余可申请金额")
    private Long remainingApplyAmount;
    @ApiModelProperty("项目剩余未付金额")
    private Long projectRemainingUnpaidAmount;
    /**
     * -----以下信息读表-----
     **/
    @ApiModelProperty("借据id")
    private Long receiptId;
    @ApiModelProperty("申请code")
    private String paymentCode;
    @ApiModelProperty("创建人id")
    private Long createBy;
    @ApiModelProperty("申请人")
    private String applicant;
    @ApiModelProperty("本次申请的付款日期")
    private LocalDate applyPaymentDate;
    @ApiModelProperty("本次申请的付款金额")
    private Long applyPaymentAmount;
    @ApiModelProperty("保证金")
    private Long earnestMoney;
    @ApiModelProperty("首期租金")
    private Long downPayment;
    @ApiModelProperty("首付款标志，0不包括，1包括")
    private Integer downPaymentType;
    @ApiModelProperty("质保金")
    private Long retentionMoney;
    @ApiModelProperty("质保金，0内扣，1不内扣")
    private Integer retentionMoneyType;
    @ApiModelProperty("服务费/咨询费")
    private Long consultingFee;
    @ApiModelProperty("名义货价")
    private Long nominalPrice;
    @ApiModelProperty("最低irr")
    private Integer lowestIrr;
    @ApiModelProperty("定价irr")
    private Integer pricingIrr;
    @ApiModelProperty("核销状态")
    private String writeOffStatus;
    @ApiModelProperty("核销人员Ids")
    private List<Long> writeOffUserIds;
    @ApiModelProperty("核销人员names")
    private List<String> writeOffUserNames;
    @ApiModelProperty("审批状态")
    private String processStatus;
    @ApiModelProperty("付款状态")
    private String paymentStatus;
    @ApiModelProperty("备注说明")
    private String remark;
    @ApiModelProperty("币种")
    private String leasedCurrency;
    @ApiModelProperty("租赁财产价值")
    private Long leasedPrice;
    /**
     * 0 隐藏勾选框/1 尚未购买保险/ 2 无需购买保险
     */
    @ApiModelProperty("保单勾选框")
    private Integer policyFlag;
    private LocalDate actualFinishDate;
    /**
     * -----查计划表-----
     **/
    @ApiModelProperty("付款计划详情")
    private List<PlanedDetailDto> planedDetails;

    @ApiModelProperty(value = "当前处理人")
    private String curAssigneeIds;
    /**
     * 补充信息
     */
    @ApiModelProperty(value = "是否结束投放（用户选择）")
    private Boolean isFinishPut;
    @ApiModelProperty(value = "是否结束投放(最终结果)")
    private Boolean isFinishPutFinal;
    @ApiModelProperty(value = "租金表收款日")
    private Integer defaultCollectionDay;
    @ApiModelProperty(value = "运营提前审核状态")
    private Integer yunyingReviewState;
    @ApiModelProperty(value = "运营提前审核状态展示文案")
    private String yunyingReviewStateDisplay;
    @ApiModelProperty(value = "运营审核通过日期")
    private LocalDate yunyingReviewDate;

    /**
     * FTP价格考核信息
     */
    private FtpAssessInfo ftpAssessInfo;

    /**
     * 拟投放金额
     */
    @ApiModelProperty("拟投放金额")
    private Long planPayAmount;
}
