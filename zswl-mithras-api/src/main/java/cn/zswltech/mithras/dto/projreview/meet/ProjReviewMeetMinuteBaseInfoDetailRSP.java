package cn.zswltech.mithras.dto.projreview.meet;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.trackEvent.TrackEventListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 项目评审会议纪要表
 * @author vico
 * @date 2025-03-18
 */
@Data
@ApiModel("项目评审会议纪要表新增-请求体")
public class ProjReviewMeetMinuteBaseInfoDetailRSP {

    private Long id;

    /**
    * 关联的评审id
    */
    @ApiModelProperty(value = "关联的评审id")
    private Long projReviewId;

    /**
    * 项目名称
    */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
    * 关联项目流程
    */
    @ApiModelProperty(value = "关联项目流程")
    private String projFlowId;

    /**
    * 会议纪要编号
    */
    @ApiModelProperty(value = "会议纪要编号")
    private String meetMinuteCode;


    /**
     * 会议纪要序号
     */
    @ApiModelProperty("会议纪要序号")
    private String meetMinuteSequence;

    /**
    * 会议纪要状态
    */
    @ApiModelProperty(value = "会议纪要状态 MeetMinuteStatuesEnum")
    private String meetMinuteStatus;

    /**
    * 租赁类型 LeaseType
    */
    @ApiModelProperty(value = "租赁类型 LeaseType")
    private String leaseType;

    /**
    * 承租人列表
    */
    @ApiModelProperty(value = "承租人列表")
    private String lesseeInfo;

    @ApiModelProperty(value = "承租人列表信息")
    List<ClientInfo> lesseeInfoDetail;

    /**
    * 供应商列表
    */
    @ApiModelProperty(value = "供应商列表")
    private String supplierInfo;

    /**
    * 租赁物要求
    */
    @ApiModelProperty(value = "租赁物要求")
    private String leaseRequire;

    /**
    * 定值依据 projectfixedvaluebasisenum#name
    */
    @ApiModelProperty(value = "定值依据 projectfixedvaluebasisenum#name")
    private String fixedValueBasis;

    /**
    * 定值依据名称 projectfixedvaluebasisenum#disply
    */
    @ApiModelProperty(value = "定值依据名称 projectfixedvaluebasisenum#disply")
    private String fixedValueBasisValue;

    /**
    * 保险安排-购买方 projectinsurancepurchaserenum
    */
    @ApiModelProperty(value = "保险安排-购买方 projectinsurancepurchaserenum")
    private String insurancePurchaser;

    /**
    * 险种 projectpolicytypeenum#name
    */
    @ApiModelProperty(value = "险种 projectpolicytypeenum#name")
    private String policyType;

    /**
    * 险种值 projectpolicytypeenum#disply
    */
    @ApiModelProperty(value = "险种值 projectpolicytypeenum#disply")
    private String policyTypeValue;

    /**
    * 保险要求 projectpolicyrequireenum#name
    */
    @ApiModelProperty(value = "保险要求 projectpolicyrequireenum#name")
    private String policyRequire;

    /**
    * 保险要求值 projectpolicyrequireenum#disply
    */
    @ApiModelProperty(value = "保险要求值 projectpolicyrequireenum#disply")
    private String policyRequireValue;

    /**
     * 保险购买时间 InsurancePurchaseTimeEnum#name
     */
    @ApiModelProperty(value = "保险购买时间")
    private String insurancePurchaseTime;

    /**
     * 保险购买时间值
     */
    @ApiModelProperty(value = "保险购买时间值")
    private String insurancePurchaseTimeValue;

    /**
     * 保险购买延迟天数 -1=放款前购买，≥0=放款后对应工作日数购买
     */
    @ApiModelProperty(value = "保险购买延迟天数")
    private String insurancePurchaseDelayDays;

    /**
    * 项目批复金额
    */
    @ApiModelProperty(value = "项目批复金额")
    private Long projectApprovalAmount;

    /**
    * 融资比例 projectfinancingratioenum#name
    */
    @ApiModelProperty(value = "融资比例 projectfinancingratioenum#name")
    private String financingRatio;

    /**
    * 融资比例值 projectfinancingratioenum#name
    */
    @ApiModelProperty(value = "融资比例值 projectfinancingratioenum#name")
    private String financingRatioValue;

    @ApiModelProperty(value = "融资要求")
    private String financingRequire;

    /**
    * 租赁期限
    */
    @ApiModelProperty(value = "租赁期限")
    private Integer leaseTerm;

    /**
    * 是否含租前期 0 不含 1 含
    */
    @ApiModelProperty(value = "是否含租前期 0 不含 1 含")
    private Integer preLeasePeriodFlag;

    /**
    * 租前期
    */
    @ApiModelProperty(value = "租前期")
    private Integer preLeasePeriod;

    /**
    * 首期租金
    */
    @ApiModelProperty(value = "首期租金")
    private Long downPayment;

    /**
    * 保证金标识 0 无，1有
    */
    @ApiModelProperty(value = "保证金标识 0 无，1有")
    private Integer earnestMoneyFlag;

    /**
    * 保证金比例
    */
    @ApiModelProperty(value = "保证金比例")
    private Integer earnestMoneyRatio;

    /**
    * 保证金金额
    */
    @ApiModelProperty(value = "保证金金额")
    private Long earnestMoneyAmount;

    /**
    * 保证金收取方式 earnestmoneycollecttypeenum#name
    */
    @ApiModelProperty(value = "保证金收取方式 earnestmoneycollecttypeenum#name")
    private String earnestMoneyCollectType;

    /**
    * 保证金收取方式名称 earnestmoneycollecttypeenum#disply
    */
    @ApiModelProperty(value = "保证金收取方式名称 earnestmoneycollecttypeenum#disply")
    private String earnestMoneyCollectTypeValue;

    /**
    * 租金支付频率 rentpaymentmethodrateenum#name
    */
    @ApiModelProperty(value = "租金支付频率 rentpaymentmethodrateenum#name")
    private String rentPaymentMethodRate;

    /**
    * 租金支付方式 rentpaymentmethodtypeenum#name
    */
    @ApiModelProperty(value = "租金支付方式 rentpaymentmethodtypeenum#name")
    private String rentPaymentMethodType;

    /**
    * 融资款支付方式 financingfundmethodtypeenum#name
    */
    @ApiModelProperty(value = "融资款支付方式 financingfundmethodtypeenum#name")
    private String financingFundMethodType;

    @ApiModelProperty(value = "融资款支付方式补充说明")
    private String financingFundSuppleRemark;

    /**
    * 资金用途
    */
    @ApiModelProperty(value = "资金用途")
    private String fundsPurpose;

    /**
    * 起租方式 rentalstartmethodenum#name
    */
    @ApiModelProperty(value = "起租方式 rentalstartmethodenum#name")
    private String rentalStartMethod;

    /**
    * 起租条件
    */
    @ApiModelProperty(value = "起租条件")
    private String rentalStartCondition;

    /**
    * 名义价款
    */
    @ApiModelProperty(value = "名义价款")
    private Long nominalPrice;

    /**
    * 决议批准文件信息 resolutiontyperateenum
    */
    @ApiModelProperty(value = "决议批准文件信息 ProjReviewMeetMinuteResolutionDTO")
    private String resolutionInfo;

    @ApiModelProperty(value = "决议批准文件信息详情")
    private List<ProjReviewMeetMinuteResolutionDTO> resolutionInfoDetails;

    /**
    * 担保措施 guaranteemeasurestypeenum
    */
    @ApiModelProperty(value = "担保措施 guaranteemeasurestypeenum")
    private String guaranteeMeasures;

    @ApiModelProperty(value = "担保措施详情")
    private List<ProjReviewMeetMinuteGuaranteeMeasuresDTO> guaranteeMeasureDetails;

    /**
    * 质押措施 pledgemeasurestypeenum
    */
    @ApiModelProperty(value = "质押措施 pledgemeasurestypeenum")
    private String pledgeMeasures;

    @ApiModelProperty(value = "质押措施 pledgemeasurestypeenum")
    private List<ProjReviewMeetMinutePledgeMeasuresDTO> pledgeMeasuresDetails;


    /**
    * 其他风险缓释措施
    */
    @ApiModelProperty(value = "其他风险缓释措施")
    private String otherRiskMitigationMeasures;

    /**
    * 特殊合同条款
    */
    @ApiModelProperty(value = "特殊合同条款")
    private String specialContractTerms;

    /**
     * 特殊合同条款
     */
    @ApiModelProperty(value = "特殊合同条款列表")
    private List<String> specialContractTermsList;

    /**
    * 放款前须落实条件
    */
    @ApiModelProperty(value = "放款前须落实条件")
    private String conditionsBeforeDisbursement;

    /**
     * 放款前须落实条件
     */
    @ApiModelProperty(value = "放款前须落实条件列表")
    private List<String> conditionsBeforeDisbursementList;

    /**
    * 管理要求-其他要求
    */
    @ApiModelProperty(value = "管理要求-其他要求")
    private String managementRequirement;

    /**
     * 管理要求-限额要求
     */
    @ApiModelProperty(value = "管理要求-限额要求")
    private String limitRequirement;

    /**
    * 报告出具时间
    */
    @ApiModelProperty(value = "报告出具时间")
    private LocalDate reportIssuanceTime;

    /**
    * 报告出具年份
    */
    @ApiModelProperty(value = "报告出具年份")
    private Integer reportIssuanceYear;

    /**
    * 报告出具次数
    */
    @ApiModelProperty(value = "报告出具次数")
    private Integer reportIssuanceNumber;

    /**
    * 报告表决人数
    */
    @ApiModelProperty(value = "报告表决人数")
    private Integer reportNumberVoters;

    /**
    * 报告同意人数
    */
    @ApiModelProperty(value = "报告同意人数")
    private Integer reportNumberAgree;

    /**
     * 报告有条件同意人数
     */
    @ApiModelProperty("报告有条件同意人数")
    private Integer reportNumberConditionalAgree;

    /**
    * 报告反对人数
    */
    @ApiModelProperty(value = "报告反对人数")
    private Integer reportNumberAgainst;

    /**
     * 表决委员
     **/
    @ApiModelProperty("表决委员")
    private String votingCommittee;

    /**
     * 表决结果 VotingResultTypeEnum
     **/
    @ApiModelProperty("表决结果")
    private String votingResult;

    /**
     * 审批有效期VotingPeriodValidityEnum
     **/
    @ApiModelProperty("审批有效期VotingPeriodValidityEnum")
    private String votingPeriodValidity;

    @ApiModelProperty("审批结束日")
    private LocalDate processEndTime;

    /**
     * 授信到期日
     **/
    @ApiModelProperty("授信到期日")
    private LocalDate creditExpirationDate;

    /**
    * 补充说明
    */
    @ApiModelProperty(value = "补充说明")
    private String suppleRemark;

    /**
     * 项目变更批复条件
     **/
    @ApiModelProperty(value = "项目变更批复条件")
    private String approvalConditionsProjectChange;

    @ApiModelProperty(value = "跟踪事项信息")
    private List<TrackEventListRSP> trackEventListRSPS;

}
