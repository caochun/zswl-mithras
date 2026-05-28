package cn.zswltech.mithras.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流程动态表单枚举
 * 有些表单是用流程接口直接显示和传入数据（如立项流程设置风控经理）
 * 有些表单是需要额外接口处理的（如上传文件、显示文件列表）
 * 需要自己与前端协商
 *
 * @author wangchuanhao
 * @date 2022/8/8 11:21 AM
 */
@AllArgsConstructor
@Getter
public enum FlowDynamicFormEnum {

    /**
     * 立项流程，风控负责人节点，补充风控经理（补充信息）
     */
    projEstablish_setRiskManager,

    /**
     * 立项流程，风控经理节点，补充文件（补充信息）
     */
    projEstablish_setRiskFile,

    /**
     * 立项流程，风控经理节点，补充文件（补充信息）其他
     */
    projEstablish_setRiskFileOther,

    /**
     * 项目评审流程，法务经理节点，补充文件（补充信息）
     */
    projReview_setLawFile,

    /**
     * 项目评审创建流程，专职评审委员节点，补充文件（补充信息）
     */
    projReview_setFullCommitteeFile,

    /**
     * 项目评审流程，风控经理节点，补充文件（补充信息）
     */
    projReview_setRiskFile,

    /**
     * 项目评审流程，风控经理节点，客户财报校验
     */
    projReview_checkCorpSubjectItem,

    /**
     * 项目评审流程，财务经理节点，补充文件（补充信息）
     */
    projReview_setFinanceFile,

    /**
     * 项目评审流程，秘书会议纪要节点，补充文件（补充信息）
     */
    projReview_setMeetingFile,

    /**
     * 项目评审流程，秘书会议记录节点，不充文件（补充信息）
     */
    projReview_setMeetingRecord,

    /**
     * 项目评审流程，秘书会议纪要汇票节点，选择当前流程是否要经过董事会
     */
    projReview_setNeedBorad,

    /**
     * 项目评审流程，董事会秘书汇票，补充文件（补充信息）
     */
    projReview_setDirectorMeetingFile,

    /**
     * 项目评审流程，秘书上会节点，选择评委会委员（审批操作）
     */
    projReview_chooseJudges,

    /**
     * 项目评审流程，评审会秘书节点，选择评审会预计召开时间
     */
    projReview_setReviewMeetingPlanDate,

    /**
     * 项目评审流程，董事会秘书节点，显示董事会成员（审批操作）
     */
    projReview_showDirectors,

    /**
     * 项目评审流程，董事会秘书节点，选择董事会预计召开时间
     */
    projReview_setDirectorMeetingPlanDate,

    /**
     * 项目评审流程，秘书会票节点，显示最近一次投票结果（审批操作）
     */
    projReview_showJudgesVotingResults,

    /**
     * 项目评审流程，董事会会票节点，显示最近一次投票结果（审批操作）
     */
    projReview_showDirectorsVotingResults,

    /**
     * 项目评审流程，会议纪要会票节点，显示最近一次投票结果（审批操作）
     */
    projReview_showMeetingVotingResults,

    /**
     * 项目评审流程，评审会秘书汇票节点，设置项目分类
     */
    projReview_setProjectClassify,

    /**
     * 定价审批流程，财务主管1，选择终审人
     */
    projReview_pricingChooseAdjudicator,

    /**
     * 定价审批流程，财务主管1，选择审批权限
     */
    projReview_pricingChooseApproveAuth,

    /**
     * 定价审批流程，财务主管2，上传定价委员会会议纪要
     */
    projReview_setPricingMeetingFile,

    /**
     * 定价审批流程，财务主管2，显示投票结果
     */
    projReview_pricingShowVoteResult,

    /**
     * 定价审批流程,总经办秘书上传会议纪要
     */
    projReview_setPricingMeetingMinutes,

    /**
     * 定价审批流程, 财务主管2上传项目收益率审查意见书
     */
    projReview_setPricingApprovalForm,

    /**
     * 合同起租/新增借据（投放），财务经理设置付款FTP
     */
    contract_setPaymentFtp,

    /**
     * 合同起租/新增投放，财务经理设置印花税
     */
    contract_updateStampDuty,

    /**
     * 合同变更-最低irr和加权平均Irr 财务经理/财务主管/财务总监岗审批
     */
    contract_changeIrrForm,

    /**
     * 付款流程，放款审核岗节点，校验评审通过是否超时
     */
    payment_checkProjreviewTimeout,

    /**
     * 付款流程，放款审核岗节点，补充文件（补充信息）
     */
    payment_setLoanApprovalFile,

    /**
     * 付款申请，财务经理节点，印花税允许编辑
     */
    payment_updateIRR,

    /**
     * 不会阻断用户操作的舆情信息检查处理器
     */
    payment_clientOpinionNotice,

    /**
     * 阻断用户操作的舆情信息检查处理器
     */
    payment_clientOpinionCheck,

    /**
     * 付款核销流程-财务经理节点补充收款信息
     */
    payment_setCollectionInfo,

    /**
     * 付款核销流程-付款补充信息展示
     */
    payment_showPaymentInfo,

    /**
     * 项目调整流程，秘书会票节点，显示最近一次投票结果（审批操作）
     */
    adjust_showJudgesVotingResults,

    /**
     * 项目调整流程，风控经理节点，补充文件（补充信息）
     */
    adjust_setRiskFile,

    /**
     * 项目调整流程，法务经理节点，补充文件（补充信息）
     */
    adjust_setLawFile,

    /**
     * 项目调整流程，秘书会议纪要节点，补充文件（补充信息）
     */
    adjust_setMeetingFile,

    /**
     * 项目调整流程，秘书会议记录节点，补充文件（补充信息）
     */
    adjust_setMeetingRecord,

    /**
     * 五级分类，资产管理岗，定性调整
     **/
    asset_classify_qualitative_adjust,

    /**
     * 五级分类，资产管理岗，确认分类
     **/
    asset_classify_result,
    /**
     * 五级分类，资产管理岗，初分分类
     **/
    asset_classify_init_result,


    ftp_chooseJudges,
    ftp_showVotingResults,
    ftp_setMeetingFile,
    ftp_setSupplement,

    /**
     * 舆情处理-黄灯-资产管理岗位打标
     */
    risk_opinion_asset_management,

    /**
     * 合同起租、付款实际核销确认提示审批人去苍穹维护合同信息
     */
    notice_addContractCode,

    /**
     * 客户评级复核节点上传文件
     */
    ratingClient_reviewSetFile,

    /**
     * 评审会秘书节点增加-项目批复金额表单
     */
    projReview_setApprovedAmount,

    /**
     * 集团评审会秘书节点增加-项目批复金额表单
     */
    group_projReview_setApprovedAmount,

    /**
     * 合同变更-提前还款流程财务确认表单
     */
    contract_early_repay_financial_confirm,

    /**
     * 合同变更-提前还款流程项目经理修改表单
     */
    contract_early_repay_start_user_modify,

    /**
     * 付款申请流程，是否需要校验审批金额标识
     */
    payment_checkApproveAmount,

    /**
     * 舆情选择处理方式
     **/
    risk_opinion_handle_type,

    /**
     * 征信报告查询经办人文件
     */
    credit_search_handle_file,

    /**
     * 租后检查计划流程资产管理岗-后续租后检查表单
     */
    follow_up_rental_inspection_form,

    /**
     * 租后检查报告（一般检查）流程，资产管理岗节点，选择资产管理复核（审批操作）
     */
    afterLeaseCheckReport_assetManager,

    /**
     * 项目评审创建流程，法务经理审批节点，选择法务经理复核（审批操作）
     */
    projReview_lawManagerReview,

    /**
     * 舆情统一监测（已放款）流程，资产管理岗节点，选择资产管理复核（审批操作）
     */
    risk_control_payment_assetManager,

    /**
     * 舆情处置（已放款）流程，资产管理岗节点，选择资产管理复核（审批操作）
     */
    risk_opinion_dispose_after_loan_assetManager,

    /**
     * 预警统一监测（已放款）流程，资产管理岗节点，选择资产管理复核（审批操作）
     */
    early_warning_monitor_after_loan_assetManager,

    /**
     * 项目评审流程，风控经理审批、法务经理审批、法务经理复核节点    否符合集团授信提款条件
     */
    projReview_setCreditWithdrawal,
    ;

    private static Map<String, FlowDynamicFormEnum> map;

    static {
        map = Stream.of(FlowDynamicFormEnum.values()).collect(Collectors.toMap(FlowDynamicFormEnum::name, e -> e));
    }


    public static FlowDynamicFormEnum getByName(String name) {
        return map.get(name);
    }

}
