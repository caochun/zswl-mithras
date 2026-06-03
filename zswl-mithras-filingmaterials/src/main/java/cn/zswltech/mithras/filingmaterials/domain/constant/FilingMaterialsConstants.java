package cn.zswltech.mithras.filingmaterials.domain.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lllin
 */
public class FilingMaterialsConstants {

    /**
     * 项目经理
     */
    public static final String TASK_NODE_1 = "userTask_filingManager";

    /**
     * 档案管理员（初审）key
     */
    public static final String TASK_NODE_CODE_2= "userTask_initialReview";

    /**
     * 档案管理员（复审）key
     */
    public static final String TASK_NODE_CODE_3= "userTask_review";

    /**
     * 流程第二岗审批节点名称
     */
    public static final String INITIAL_REVIEW = "档案管理岗（经办）：";
    /**
     * 流程第三岗审批节点名称
     */
    public static final String REVIEW = "档案管理岗（复核）：";

    /**
     * 审批节点映射(流程定义时请将节点名称不能重复）
     */
    public static final Map<String,String> ACTIVITY_MAP = new HashMap<String, String>() {{
        put(TASK_NODE_CODE_2, "档案管理岗（经办）：");
        put(TASK_NODE_CODE_3, "档案管理岗（复核）：");
        put(USER_TASK_AFTER_REVIEW, "接收人：");
        put(USER_TASK_OTHER_REVIEW, "档案管理岗：");
        put(USER_TASK_OTHER_INIT_REVIEW, "档案管理岗：");
        put(USER_TASK_OTHER_REVIEW_02, "档案管理岗：");
    }};

    /**
     * 审批节点映射
     */
    public static final Map<String, List<String>> TASK_ACTIVITY_LIST = new HashMap<String, List<String>>() {{
        put(TASK_NODE_CODE_2, Arrays.asList(TASK_NODE_CODE_2));
        put(TASK_NODE_CODE_3, Arrays.asList(TASK_NODE_CODE_3));
        put(USER_TASK_AFTER_REVIEW, Arrays.asList(USER_TASK_AFTER_REVIEW));
        put(USER_TASK_OTHER_REVIEW, Arrays.asList(USER_TASK_OTHER_REVIEW));
        put(USER_TASK_OTHER_INIT_REVIEW, Arrays.asList(USER_TASK_OTHER_INIT_REVIEW));
        put(USER_TASK_OTHER_REVIEW_02, Arrays.asList(USER_TASK_OTHER_INIT_REVIEW,USER_TASK_OTHER_REVIEW_02));
    }};

    /**
     * 档案管理（经办）审批人配置
     */
    public static final String FILING_FLOW_INIT_REVIEW_USER = "filingFlowInitReviewUser";


    /**
     * 项目资料归档流程-邮件接收人
     */
    public static final String FILING_FLOW_EMAIL_USER = "filingFlowEmailUser";

    /**
     * 项目资料归档流程-邮件接收人
     */
    public static final String FILING_FLOW_SUPPLEMENT_FOLLOW_UP_EMAIL_USER = "filingFlowSupplementFollowUpEmailUser";

    /**
     * 经办人
     */
    public static final Long FILE_MANAGEMENT_HANDLER= 150L;

    /**
     * 运营管理
     */
    public static final String YYGLB = "YYGLB";

    /**
     * 资料清单
     */
    public static final String BASIC_INFORMATION = "BASIC_INFORMATION";

    /**
     * 归档资料压缩包
     */
    public static final String ARCHIVED_DOCUMENT_PACKAGE = "ARCHIVED_DOCUMENT_PACKAGE";
    /**
     * 基本-参考-其他
     */
    public static final String OTHERS = "OTHERS";

    /**
     * 归档-其他资料清单
     */
    public static final String OTHER = "OTHER";

    public static final String CLIENT_ID = "clientId";
    public static final String TEMPLATE_TYPE= "templateType";
    public static final String CONTRACT_ID= "contractId";
    public static final String INNER_TABLE_LIST = "innerTableList";
    public static final String CONTRACT_CODE = "contractCode";
    public static final String BELONG_NAME = "belongName";
    public static final String PROJ_NAME = "projName";
    public static final String CLIENT_NAME = "clientName";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String MODEL_NAME = "modelName";
    public static final String LIST = "list";
    public static final String GENERATE_MANAGE_FLAG = "generateManageFlag";
    public static final String OBJECT = "object";
    public static final String YEAR = "year";
    public static final String PHASE = "phase";

    /**
     * 关联类型-项目
     */
    public static final String OBJECT_TYPE_PROJECT = "PROJECT";
    /**
     * 关联类型-资金
     */
    public static final String OBJECT_TYPE_FUND= "FUND";

    /**
     * 资产管理岗
     */
    public static final String USER_TASK_ASSET_MANAGER = "userTask_assetManager";
    public static final String USER_TASK_AFTER_REVIEW = "userTask_afterReview";

    public static final String USER_TASK_OTHER_REVIEW = "userTask_otherReview";
    public static final String USER_TASK_OTHER_REVIEW_02 = "userTask_otherReview02";
    public static final String USER_TASK_OTHER_INIT_REVIEW = "userTask_otherInitReview";
    public static final String OTHER_USER_TASK_START_USER = "userTask_startUser";

    /**
     * 项目经理
     */
    public static final String XMJL = "XMJL";
    /**
     * 运营经理
     */
    public static final String YYGLB_YYJL = "YYGLB_YYJL";
    /**
     * 评审会秘书
     */
    public static final String PSHMS = "PSHMS";

    /**
     * 资料清单
     */
    public static final String IND_BASIC_INFORMATION = "IND_BASIC_INFORMATION";

    /**
     * 文件校验code
     */
    public static final String FILE_CHECK = "FILE_CHECK";


    /**
     * 流程校验code前缀
     */
    public static final String APPROVAL = "APPROVAL";
}
