package cn.zswltech.mithras.service.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 流程
 *
 * @author wangchuanhao
 * @date 2022/6/21 11:03 PM
 */
public class FlowConstants {

    /**
     * 客户修改审批流
     */
    public static final String CLIENT_MODIFY_FLOW = "ClientModifyFlow";

    /**
     * 立项（变更）审批
     */
    public static final String PROJ_ESTABLISH_MODIFY_FLOW = "ProjEstablishModifyFlow";
    /**
     * 项目评审
     */
    public static final String PROJ_REVIEW_FLOW = "ProjReviewFlow";

    /**
     * 用户任务 发起人节点id
     */
    public static final String START_USER_TASK = "userTask_startUser";

    public static final String PROJECT_MANAGER = "project_manager";

    /**
     * 运营部经理
     */
    public static final String OPERATION_MANAGEMENT = "operationManagement";

    /**
     * 用户任务 风控经理
     */
    public static final String RISK_MANAGER_TASK = "userTask_riskManager";

    /**
     * 项目评审 是否评审会退回参数名称（流程参数名称）
     */
    public static final String PROJ_REVIEW_IS_REVIEW_MEETING_BACK = "isReviewMeetingBack";

    /**
     * 项目评审 是否处于发起人复议中（流程参数名称）
     */
    public static final String PROJ_REVIEW_IS_START_USER_RECONSIDERATION = "isStartUserReconsideration";

    /**
     * 项目评审 发起人操作选择参数名称（仅在被评审会退回时设置参数）
     */
    public static final String PROJ_REVIEW_START_USER_CHOICE = "startUserChoice";

    /**
     * 项目评审 发起人操作选择"提交"（仅在被评审会退回时设置参数）
     */
    public static final String PROJ_REVIEW_START_USER_CHOICE_COMMIT = "commit";

    /**
     * 项目评审 发起人操作选择"复议"（仅在被评审会退回时设置参数）
     */
    public static final String PROJ_REVIEW_START_USER_CHOICE_RECONSIDERATION = "reconsideration";

    public static final String PROJ_REVIEW_SECRETARY_CHOICE = "secretaryChoice";

    public static final String PROJ_REVIEW_SECRETARY_CHOICE_AGREE = "agree";

    public static final String PROJ_REVIEW_SECRETARY_CHOICE_DISAGREE = "disagree";

    public static final String PROJ_REVIEW_SECRETARY_CHOICE_CONDITION_AGREE = "conditionAgree";

    public static final String PARALLEL_RISK_MANAGER = "userTask_riskManager";
    public static final String PARALLEL_LAW_MANAGER = "userTask_lawManager";
    public static final String PARALLEL_RISK_MANAGER_BACK = "userTask_riskManager_back";
    public static final String PARALLEL_LAW_MANAGER_BACK = "userTask_lawManager_back";
    public static final String JURY_SECRETARY_COLLECT = "userTask_jurySecretaryCollect";
    public static final String PARALLEL_LAW_MANAGER_REVIEW = "userTask_lawManager_review";
    public static final String PARALLEL_LAW_MANAGER_BACK_REVIEW = "userTask_lawManager_back_review";

    public static final List<String> PARALLEL_ACTIVITY_ID_LIST = Arrays.asList(PARALLEL_RISK_MANAGER, PARALLEL_LAW_MANAGER, PARALLEL_RISK_MANAGER_BACK, PARALLEL_LAW_MANAGER_BACK);

    /**
     * 舆情处置（已放款） 红灯场景，资产管理岗节点ID
     */
    public static final String RISK_OPINION_DISPOSE_AFTER_LOAN_RED_LIGHT = "Activity_0j8u26a";

    /**
     * 舆情处置（已放款） 黄灯或其他场景，资产管理岗节点ID
     */
    public static final String RISK_OPINION_DISPOSE_AFTER_LOAN_OTHER_LIGHT = "Activity_0nfhd19";

    /**
     * 预警统一监测（已放款） 红灯场景，资产管理岗节点ID
     */
    public static final String EARLY_WARNING_MONITOR_AFTER_LOAN_RED_LIGHT = "Activity_0j8u26a";

    /**
     * 预警统一监测（已放款） 黄灯或其他场景，资产管理岗节点ID
     */
    public static final String EARLY_WARNING_MONITOR_AFTER_LOAN_OTHER_LIGHT = "Activity_0nfhd19";

    /**
     * 舆情统一监测（已放款） 红灯场景，资产管理岗节点ID
     */
    public static final String RISK_CONTROL_PAYMENT_ASSET_MANAGEMENT_RED_LIGHT = "Activity_0j8u26a";

    /**
     * 舆情统一监测（已放款） 黄灯或其他场景，资产管理岗节点ID
     */
    public static final String RISK_CONTROL_PAYMENT_ASSET_MANAGEMENT_OTHER_LIGHT = "Activity_0nfhd19";

    /**
     * 授信评审创建、 授信评审变更     “风控经理审批”、“法务经理审批”、“法务经理复核”   否符合集团授信提款条件
     */
    public static final String GROUP_CREDIT_WITHDRAWAL_LAW = "groupCreditWithdrawalLaw";
    public static final String GROUP_CREDIT_WITHDRAWAL_RISK = "groupCreditWithdrawalRisk";
}
