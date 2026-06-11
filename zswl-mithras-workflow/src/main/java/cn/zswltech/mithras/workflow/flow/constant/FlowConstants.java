package cn.zswltech.mithras.workflow.flow.constant;

import java.util.Arrays;
import java.util.List;

public class FlowConstants {

    public static final String CLIENT_MODIFY_FLOW = "ClientModifyFlow";

    public static final String PROJ_ESTABLISH_MODIFY_FLOW = "ProjEstablishModifyFlow";

    public static final String PROJ_REVIEW_FLOW = "ProjReviewFlow";

    public static final String START_USER_TASK = "userTask_startUser";

    public static final String PROJECT_MANAGER = "project_manager";

    public static final String OPERATION_MANAGEMENT = "operationManagement";

    public static final String RISK_MANAGER_TASK = "userTask_riskManager";

    public static final String PROJ_REVIEW_IS_REVIEW_MEETING_BACK = "isReviewMeetingBack";

    public static final String PROJ_REVIEW_IS_START_USER_RECONSIDERATION = "isStartUserReconsideration";

    public static final String PROJ_REVIEW_START_USER_CHOICE = "startUserChoice";

    public static final String PROJ_REVIEW_START_USER_CHOICE_COMMIT = "commit";

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

    public static final String RISK_OPINION_DISPOSE_AFTER_LOAN_RED_LIGHT = "Activity_0j8u26a";

    public static final String RISK_OPINION_DISPOSE_AFTER_LOAN_OTHER_LIGHT = "Activity_0nfhd19";

    public static final String EARLY_WARNING_MONITOR_AFTER_LOAN_RED_LIGHT = "Activity_0j8u26a";

    public static final String EARLY_WARNING_MONITOR_AFTER_LOAN_OTHER_LIGHT = "Activity_0nfhd19";

    public static final String RISK_CONTROL_PAYMENT_ASSET_MANAGEMENT_RED_LIGHT = "Activity_0j8u26a";

    public static final String RISK_CONTROL_PAYMENT_ASSET_MANAGEMENT_OTHER_LIGHT = "Activity_0nfhd19";

    public static final String GROUP_CREDIT_WITHDRAWAL_LAW = "groupCreditWithdrawalLaw";
    public static final String GROUP_CREDIT_WITHDRAWAL_RISK = "groupCreditWithdrawalRisk";
}
