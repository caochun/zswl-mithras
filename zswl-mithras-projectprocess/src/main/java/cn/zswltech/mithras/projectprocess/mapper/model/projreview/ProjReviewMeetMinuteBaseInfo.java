package cn.zswltech.mithras.projectprocess.mapper.model.projreview;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 项目评审会议纪要表
 * @author vico
 * @date 2025-03-18
 */
@Data
public class ProjReviewMeetMinuteBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 关联的评审id
    */
    @TableField("proj_review_id")
    private Long projReviewId;

    /**
     * AppProjStageStatus
     **/
    @TableField("proj_review_type")
    private String projReviewType;

    /**
    * 项目名称
    */
    @TableField("proj_name")
    private String projName;

    /**
    * 关联项目流程
    */
    @TableField("proj_flow_id")
    private String projFlowId;

    /**
    * 会议纪要编号
    */
    @TableField("meet_minute_code")
    private String meetMinuteCode;

    /**
     * 会议纪要序号
     */
    @TableField("meet_minute_sequence")
    private String meetMinuteSequence;

    /**
    * 会议纪要状态 MeetMinuteStatuesEnum
    */
    @TableField("meet_minute_status")
    private String meetMinuteStatus;

    /**
    * 租赁类型 List<LeaseType>
    */
    @TableField("lease_type")
    private String leaseType;

    /**
    * 承租人列表
    */
    @TableField("lessee_info")
    private String lesseeInfo;

    /**
     * 融资比例特殊要求
     */
    @TableField("fund_special_requirements")
    private String fundSpecialRequirements;

    /**
    * 供应商列表
    */
    @TableField("supplier_info")
    private String supplierInfo;

    /**
    * 租赁物要求
    */
    @TableField("lease_require")
    @IncludeNull
    private String leaseRequire;

    /**
    * 定值依据 projectfixedvaluebasisenum#name
    */
    @TableField("fixed_value_basis")
    private String fixedValueBasis;

    /**
    * 定值依据名称 projectfixedvaluebasisenum#disply
    */
    @TableField("fixed_value_basis_value")
    @IncludeNull
    private String fixedValueBasisValue;

    /**
    * 保险安排-购买方 projectinsurancepurchaserenum
    */
    @TableField("insurance_purchaser")
    private String insurancePurchaser;

    /**
    * 险种 projectpolicytypeenum#name
    */
    @TableField("policy_type")
    private String policyType;

    /**
    * 险种值 projectpolicytypeenum#disply
    */
    @TableField("policy_type_value")
    @IncludeNull
    private String policyTypeValue;

    /**
    * 保险要求 projectpolicyrequireenum#name
    */
    @TableField("policy_require")
    private String policyRequire;

    /**
    * 保险要求值 projectpolicyrequireenum#disply
    */
    @TableField("policy_require_value")
    @IncludeNull
    private String policyRequireValue;

    /**
     * 保险购买时间 InsurancePurchaseTimeEnum#name
     */
    @TableField("insurance_purchase_time")
    @IncludeNull
    private String insurancePurchaseTime;

    /**
     * 保险购买时间值
     */
    @TableField("insurance_purchase_time_value")
    @IncludeNull
    private String insurancePurchaseTimeValue;

    /**
    * 项目批复金额
    */
    @TableField("project_approval_amount")
    private Long projectApprovalAmount;

    /**
    * 融资比例 projectfinancingratioenum#name
    */
    @TableField("financing_ratio")
    private String financingRatio;

    /**
    * 融资比例值 projectfinancingratioenum#name
    */
    @TableField("financing_ratio_value")
    private String financingRatioValue;

    //融资要求
    @TableField("financing_require")
    @IncludeNull
    private String financingRequire;

    /**
    * 租赁期限
    */
    @TableField("lease_term")
    private Integer leaseTerm;

    /**
    * 是否含租前期 0 不含 1 含
    */
    @TableField("pre_lease_period_flag")
    private Integer preLeasePeriodFlag;

    /**
    * 租前期
    */
    @TableField("pre_lease_period")
    private Integer preLeasePeriod;

    /**
    * 首期租金
    */
    @TableField("down_payment")
    private Long downPayment;

    /**
    * 保证金标识 0 无，1有
    */
    @TableField("earnest_money_flag")
    private Integer earnestMoneyFlag;

    /**
    * 保证金比例
    */
    @TableField("earnest_money_ratio")
    private Integer earnestMoneyRatio;

    /**
    * 保证金金额
    */
    @TableField("earnest_money_amount")
    private Long earnestMoneyAmount;

    /**
    * 保证金收取方式 earnestmoneycollecttypeenum#name
    */
    @TableField("earnest_money_collect_type")
    private String earnestMoneyCollectType;

    /**
    * 保证金收取方式名称 earnestmoneycollecttypeenum#disply
    */
    @TableField("earnest_money_collect_type_value")
    private String earnestMoneyCollectTypeValue;

    /**
    * 租金支付频率 rentpaymentmethodrateenum#name
    */
    @TableField("rent_payment_method_rate")
    private String rentPaymentMethodRate;

    /**
    * 租金支付方式 rentpaymentmethodtypeenum#name
    */
    @TableField("rent_payment_method_type")
    private String rentPaymentMethodType;

    /**
    * 融资款支付方式 financingfundmethodtypeenum#name
    */
    @TableField("financing_fund_method_type")
    private String financingFundMethodType;

    @TableField("financing_fund_supple_remark")
    @IncludeNull
    private String financingFundSuppleRemark;

    /**
    * 资金用途
    */
    @TableField("funds_purpose")
    @IncludeNull
    private String fundsPurpose;

    /**
    * 起租方式 rentalstartmethodenum#name
    */
    @TableField("rental_start_method")
    private String rentalStartMethod;

    /**
    * 起租条件
    */
    @TableField("rental_start_condition")
    @IncludeNull
    private String rentalStartCondition;

    /**
    * 名义价款
    */
    @TableField("nominal_price")
    private Long nominalPrice;

    /**
    * 决议批准文件信息 ProjReviewMeetMinuteResolutionDTO resolutiontyperateenum
    */
    @TableField("resolution_info")
    @IncludeNull
    private String resolutionInfo;

    /**
    * 担保措施 ProjReviewMeetMinuteGuaranteeMeasuresDTO GuaranteeMeasuresTypeEnum
    */
    @TableField("guarantee_measures")
    @IncludeNull
    private String guaranteeMeasures;

    /**
    * 质押措施 ProjReviewMeetMinutePledgeMeasuresDTO pledgemeasurestypeenum
    */
    @TableField("pledge_measures")
    @IncludeNull
    private String pledgeMeasures;

    /**
    * 其他风险缓释措施
    */
    @TableField("other_risk_mitigation_measures")
    @IncludeNull
    private String otherRiskMitigationMeasures;

    /**
    * 特殊合同条款
    */
    @TableField("special_contract_terms")
    @IncludeNull
    private String specialContractTerms;

    /**
    * 放款前须落实条件
    */
    @TableField("conditions_before_disbursement")
    @IncludeNull
    private String conditionsBeforeDisbursement;

    /**
    * 管理要求-其他要求
    */
    @TableField("management_requirement")
    @IncludeNull
    private String managementRequirement;

    /**
     * 管理要求-限额要求
     */
    @TableField("limit_requirement")
    @IncludeNull
    private String limitRequirement;

    /**
    * 报告出具时间
    */
    @TableField("report_issuance_time")
    private LocalDate reportIssuanceTime;

    /**
    * 报告出具年份
    */
    @TableField("report_issuance_year")
    private Integer reportIssuanceYear;

    /**
     * 报告出具次数
     */
    @TableField("report_issuance_number")
    private Integer reportIssuanceNumber;

    /**
    * 报告表决人数
    */
    @TableField("report_number_voters")
    private Integer reportNumberVoters;

    /**
    * 报告同意人数
    */
    @TableField("report_number_agree")
    private Integer reportNumberAgree;


    /**
     * 报告有条件同意人数
     */
    @TableField("report_number_conditional_agree")
    private Integer reportNumberConditionalAgree;


    /**
    * 报告反对人数
    */
    @TableField("report_number_against")
    private Integer reportNumberAgainst;

    /**
     * 表决委员
     **/
    @TableField("voting_committee")
    private String votingCommittee;

    /**
     * 表决结果 VotingResultTypeEnum
     **/
    @TableField("voting_result")
    private String votingResult;

    /**
     * 审批有效期VotingPeriodValidityEnum
     **/
    @TableField("voting_period_validity")
    private String votingPeriodValidity;

    /**
     * 流程结束时间
     **/
    @TableField("process_end_time")
    private LocalDate processEndTime;

    /**
     * 补充说明
     */
    @TableField("supple_remark")
    @IncludeNull
    private String suppleRemark;

    /**
     * 项目变更批复条件
     **/
    @TableField("approval_conditions_project_change")
    @IncludeNull
    private String approvalConditionsProjectChange;

}
