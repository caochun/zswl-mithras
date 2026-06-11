package cn.zswltech.mithras.foundation.constant;

import java.util.*;

/**
 * @author: ldhu
 * @Data: 2025/11/27
 * @desc:
 */
public class Constant {
    public static final String ratingTypeFirst = "初评";
    public static final String ratingTypeOther = "复评";

    public static final String processStatusPass = "APPROVAL_PASS";


    public static final String hymxCode = "client_hymx"; //航运模型编码
    public static final String hymxName = "航运模型";
    public static final String hymxBankClientFlag = "is_intragroup_customer"; //航运模型是否本行员工
    public static final Integer hymxUpLevel = 2; //本行员工提升两个等级
    public static final Long hymxIndexStart = 500000000000L; // 避免id于行内客户重复  航运客户id初始化设置为大数
    public static final String hymxParamInfo = "[{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：存续年限超70年\",\"value\":\"A\"},{\"label\":\"B：存续年限50（含）-70年\",\"value\":\"B\"},{\"label\":\"C：存续年限20（含）-50年且穿越过2个完整周期\",\"value\":\"C\"},{\"label\":\"D：存续年限10（含）-20年且穿越过1个完整周期\",\"value\":\"D\"},{\"label\":\"E：其他\",\"value\":\"E\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"历史穿越周期次数/存续年限\",\"fieldName\":\"historical_cycle_count_survival_years\",\"groupCode\":\"ongoing_concern_ability\",\"groupName\":\"定性指标-持续经营能力\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：0分\",\"value\":\"A\"},{\"label\":\"B：1分\",\"value\":\"B\"},{\"label\":\"C：2分\",\"value\":\"C\"},{\"label\":\"D：3分\",\"value\":\"D\"},{\"label\":\"E：4分\",\"value\":\"E\"},{\"label\":\"F：5分\",\"value\":\"F\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"实控人（含家族）/核心高管从业经验\",\"fieldName\":\"actual_controller_core_senior_management_experience\",\"groupCode\":\"ongoing_concern_ability\",\"groupName\":\"定性指标-持续经营能力\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：0分\",\"value\":\"A\"},{\"label\":\"B：1分\",\"value\":\"B\"},{\"label\":\"C：2分\",\"value\":\"C\"},{\"label\":\"D：3分\",\"value\":\"D\"},{\"label\":\"E：4分\",\"value\":\"E\"},{\"label\":\"F：5分\",\"value\":\"F\"},{\"label\":\"G：6分\",\"value\":\"G\"},{\"label\":\"H：7分\",\"value\":\"H\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"市场口碑\",\"fieldName\":\"market_reputation\",\"groupCode\":\"ongoing_concern_ability\",\"groupName\":\"定性指标-持续经营能力\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：央企、或国际知名上市公司 \",\"value\":\"A\"},{\"label\":\"B：省属国企、国际主流证券交易所或国内主板上市但不符合第一档标准的船东\",\"value\":\"B\"},{\"enumList\":[{\"label\":\"10分\",\"value\":\"A\"},{\"label\":\"11分\",\"value\":\"B\"},{\"label\":\"12分\",\"value\":\"C\"}],\"label\":\"C：不符合第一、二档的上市公司、虽未上市但公司结构清晰且具有较高知名度的船东\",\"value\":\"C\"},{\"enumList\":[{\"label\":\"7分\",\"value\":\"A\"},{\"label\":\"8分\",\"value\":\"B\"},{\"label\":\"9分\",\"value\":\"C\"}],\"label\":\"D：经营国际业务且国际化程度较高的船东\",\"value\":\"D\"},{\"enumList\":[{\"label\":\"5分\",\"value\":\"A\"},{\"label\":\"6分\",\"value\":\"B\"}],\"label\":\"E: 国内未上市民营船东（经营国内业务为主，或涉及国际业务但国际化程度较低）  \",\"value\":\"E\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"股东背景&公司治理\",\"fieldName\":\"shareholder_background_corporate_governance\",\"groupCode\":\"customer_background_scale\",\"groupName\":\"定性指标-客户背景/规模\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：头部\",\"value\":\"A\"},{\"label\":\"B：中部（不含）-头部（不含）\",\"value\":\"B\"},{\"label\":\"C：中部\",\"value\":\"C\"},{\"label\":\"D：中部以下\",\"value\":\"D\"},{\"label\":\"E: 底部 \",\"value\":\"E\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"细分市场排名\",\"fieldName\":\"niche_market_ranking\",\"groupCode\":\"customer_background_scale\",\"groupName\":\"定性指标-客户背景/规模\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"enumList\":[{\"label\":\"1分\",\"value\":\"A\"},{\"label\":\"2分\",\"value\":\"B\"}],\"label\":\"A：资产小于5亿人民币/年主营业务收入小于2亿元人民币 \",\"value\":\"A\"},{\"label\":\"B：资产5-10亿元人民币/年主营业务收入2-5亿元人民币\",\"value\":\"B\"},{\"label\":\"C：资产10-50亿元人民币/年主营业务收入5-20亿元人民币\",\"value\":\"C\"},{\"enumList\":[{\"label\":\"7分\",\"value\":\"A\"},{\"label\":\"8分\",\"value\":\"B\"}],\"label\":\"D：资产50-500亿元人民币/年主营业务收入20-200亿元人民币\",\"value\":\"D\"},{\"label\":\"E: 资产大于500亿元人民币/年主营业务收入大于200亿元人民币\",\"value\":\"E\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"资产营收规模\",\"fieldName\":\"asset_revenue_scale\",\"groupCode\":\"customer_background_scale\",\"groupName\":\"定性指标-客户背景/规模\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：低于2倍\",\"value\":\"A\"},{\"label\":\"B：2（含）-5倍\",\"value\":\"B\"},{\"label\":\"C：5（含）-10倍\",\"value\":\"C\"},{\"label\":\"D：10（含）-20倍\",\"value\":\"D\"},{\"label\":\"E: 超过20倍（含） \",\"value\":\"E\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"净资产覆盖本金倍数（净资产/我司风险敞口）\",\"fieldName\":\"net_asset_principal_coverage_multiple\",\"groupCode\":\"financial_indicators\",\"groupName\":\"定性指标-财务指标\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：20%-40%\",\"value\":\"A\"},{\"label\":\"B：40%-60%\",\"value\":\"B\"},{\"label\":\"C：60%-80%\",\"value\":\"C\"},{\"label\":\"D：80%-100%\",\"value\":\"D\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"资产负债率\",\"fieldName\":\"asset_liability_ratio\",\"groupCode\":\"financial_indicators\",\"groupName\":\"定性指标-财务指标\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：新造外贸散货船、油化船主流船型\",\"value\":\"A\"},{\"label\":\"B：0-5岁外贸集散油气主流船型（不含第一档船型），新造内贸集散油气\",\"value\":\"B\"},{\"label\":\"C：5-15岁外贸集散油气主流船型+0-10岁内贸二手集散油气+0-10岁外贸其他船型（mpp、重吊、集散油气中非主流船型等等）\",\"value\":\"C\"},{\"label\":\"D：不能认定为上述三档的其他船型\",\"value\":\"D\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"船型\",\"fieldName\":\"vessel_type\",\"groupCode\":\"subject_vessel\",\"groupName\":\"定性指标-标的船舶\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：大于200%（含）\",\"value\":\"A\"},{\"label\":\"B：150%（含）-200%（含）\",\"value\":\"B\"},{\"label\":\"C：120%（含）-150%\",\"value\":\"C\"},{\"label\":\"D：100（含）-120%\",\"value\":\"D\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"单船租金覆盖比（EBITDA/平均光租租金）\",\"fieldName\":\"single_vessel_rental_coverage_ratio\",\"groupCode\":\"subject_vessel\",\"groupName\":\"定性指标-标的船舶\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：国际一流租家的租约以及覆盖度达到100%以上\",\"value\":\"A\"},{\"label\":\"B：国际一流租家的租约或租期覆盖度达到50%-100%\",\"value\":\"B\"},{\"label\":\"C：非国际一流租家的租约或租期覆盖度在0%-50%\",\"value\":\"C\"},{\"label\":\"D：无租约\",\"value\":\"D\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"超过12个月的次租约\",\"fieldName\":\"sublease_exceeding_12_months\",\"groupCode\":\"subject_vessel\",\"groupName\":\"定性指标-标的船舶\",\"isAreaModelIndex\":false,\"isChange\":false},{\"canEdit\":false,\"dataType\":\"enum\",\"enumList\":[{\"label\":\"A：是\",\"value\":\"Y\"},{\"label\":\"B：否\",\"value\":\"N\"}],\"fetchMethod\":\"IMPORT\",\"fieldComment\":\"该客户是否为集团内客户\",\"fieldName\":\"is_intragroup_customer\",\"groupCode\":\"main_ruler_adjustment_item\",\"groupName\":\"评级调整事项-主标尺调整项\",\"isAreaModelIndex\":false,\"isChange\":false}]";


    /*标的物得分  其他的为客户综合得分*/
    public static Set subjectVesselSet = new HashSet<>();
    public static Map<String, Integer> hymxScoreMap = new HashMap<>();  //评分规则

    static {
        subjectVesselSet.add("vessel_type"); //船型（15分）
        subjectVesselSet.add("single_vessel_rental_coverage_ratio"); //单船租金覆盖比（10分） EBITDA/平均光租租金
        subjectVesselSet.add("sublease_exceeding_12_months"); //超过12个月的次租约（3分）

        hymxScoreMap.put("historical_cycle_count_survival_years_A", 15);
        hymxScoreMap.put("historical_cycle_count_survival_years_B", 12);
        hymxScoreMap.put("historical_cycle_count_survival_years_C", 9);
        hymxScoreMap.put("historical_cycle_count_survival_years_D", 6);
        hymxScoreMap.put("historical_cycle_count_survival_years_E", 3);
        hymxScoreMap.put("actual_controller_core_senior_management_experience_A", 0);
        hymxScoreMap.put("actual_controller_core_senior_management_experience_B", 1);
        hymxScoreMap.put("actual_controller_core_senior_management_experience_C", 2);
        hymxScoreMap.put("actual_controller_core_senior_management_experience_D", 3);
        hymxScoreMap.put("actual_controller_core_senior_management_experience_E", 4);
        hymxScoreMap.put("actual_controller_core_senior_management_experience_F", 5);
        hymxScoreMap.put("market_reputation_A", 0);
        hymxScoreMap.put("market_reputation_B", 1);
        hymxScoreMap.put("market_reputation_C", 2);
        hymxScoreMap.put("market_reputation_D", 3);
        hymxScoreMap.put("market_reputation_E", 4);
        hymxScoreMap.put("market_reputation_F", 5);
        hymxScoreMap.put("market_reputation_G", 6);
        hymxScoreMap.put("market_reputation_H", 7);

        hymxScoreMap.put("shareholder_background_corporate_governance_A", 15);
        hymxScoreMap.put("shareholder_background_corporate_governance_B", 13);
        hymxScoreMap.put("shareholder_background_corporate_governance_C", 0);
        hymxScoreMap.put("shareholder_background_corporate_governance_C_A", 10);
        hymxScoreMap.put("shareholder_background_corporate_governance_C_B", 11);
        hymxScoreMap.put("shareholder_background_corporate_governance_C_C", 12);
        hymxScoreMap.put("shareholder_background_corporate_governance_D", 0);
        hymxScoreMap.put("shareholder_background_corporate_governance_D_A", 7);
        hymxScoreMap.put("shareholder_background_corporate_governance_D_B", 8);
        hymxScoreMap.put("shareholder_background_corporate_governance_D_C", 9);
        hymxScoreMap.put("shareholder_background_corporate_governance_E", 0);
        hymxScoreMap.put("shareholder_background_corporate_governance_E_A", 5);
        hymxScoreMap.put("shareholder_background_corporate_governance_E_B", 6);
        hymxScoreMap.put("niche_market_ranking_A", 5);
        hymxScoreMap.put("niche_market_ranking_B", 4);
        hymxScoreMap.put("niche_market_ranking_C", 3);
        hymxScoreMap.put("niche_market_ranking_D", 2);
        hymxScoreMap.put("niche_market_ranking_E", 1);
        hymxScoreMap.put("asset_revenue_scale_A", 0);
        hymxScoreMap.put("asset_revenue_scale_A_A", 1);
        hymxScoreMap.put("asset_revenue_scale_A_B", 2);
        hymxScoreMap.put("asset_revenue_scale_B", 3);
        hymxScoreMap.put("asset_revenue_scale_C", 5);
        hymxScoreMap.put("asset_revenue_scale_D", 0);
        hymxScoreMap.put("asset_revenue_scale_D_A", 7);
        hymxScoreMap.put("asset_revenue_scale_D_B", 8);
        hymxScoreMap.put("asset_revenue_scale_E", 10);

        hymxScoreMap.put("net_asset_principal_coverage_multiple_A", 2);
        hymxScoreMap.put("net_asset_principal_coverage_multiple_B", 4);
        hymxScoreMap.put("net_asset_principal_coverage_multiple_C", 6);
        hymxScoreMap.put("net_asset_principal_coverage_multiple_D", 8);
        hymxScoreMap.put("net_asset_principal_coverage_multiple_E", 10);
        hymxScoreMap.put("asset_liability_ratio_A", 5);
        hymxScoreMap.put("asset_liability_ratio_B", 4);
        hymxScoreMap.put("asset_liability_ratio_C", 3);
        hymxScoreMap.put("asset_liability_ratio_D", 2);

        hymxScoreMap.put("vessel_type_A", 15);
        hymxScoreMap.put("vessel_type_B", 12);
        hymxScoreMap.put("vessel_type_C", 8);
        hymxScoreMap.put("vessel_type_D", 4);
        hymxScoreMap.put("single_vessel_rental_coverage_ratio_A", 10);
        hymxScoreMap.put("single_vessel_rental_coverage_ratio_B", 8);
        hymxScoreMap.put("single_vessel_rental_coverage_ratio_C", 5);
        hymxScoreMap.put("single_vessel_rental_coverage_ratio_D", 2);
        hymxScoreMap.put("sublease_exceeding_12_months_A", 3);
        hymxScoreMap.put("sublease_exceeding_12_months_B", 2);
        hymxScoreMap.put("sublease_exceeding_12_months_C", 1);
        hymxScoreMap.put("sublease_exceeding_12_months_D", 0);
    }


    /*租赁物内部查重 不同类型租赁物需要的匹配不同的字段*/
    public static Map<String, List<String>> leaseItemDedupTypeParam = new HashMap<>();
    public static Map<String, String> approveStatusMap = new HashMap<>();

    static {
        leaseItemDedupTypeParam.put("BUS", Arrays.asList("车架号/规格型号", "发票号")); // 公交车
        leaseItemDedupTypeParam.put("MOTOR_VEHICLE", Arrays.asList("名称", "车架号/规格型号", "发票号"));// 机动车
        leaseItemDedupTypeParam.put("VESSEL", Arrays.asList("租赁船舶名称", "船舶识别号", "IMO编号")); // 船舶
        leaseItemDedupTypeParam.put("COMMUNICATION_BASE_STATION", Arrays.asList("基站名称", "经度", "纬度")); // 通信基站

        leaseItemDedupTypeParam.put("PRODUCTION_EQUIPMENT", Arrays.asList("生产线名称", "设备名称", "发票号")); // 生产设备
        leaseItemDedupTypeParam.put("OTHER", Arrays.asList("生产线名称", "设备名称", "发票号")); // 其他
        leaseItemDedupTypeParam.put("POWER_STATION", Arrays.asList("生产线名称", "设备名称", "发票号")); // 电站
        leaseItemDedupTypeParam.put("ENVIRONMENT_PROTECTION", Arrays.asList("生产线名称", "设备名称", "发票号")); // 环保
        leaseItemDedupTypeParam.put("PRIVATE_EDUCATION", Arrays.asList("生产线名称", "设备名称", "发票号")); // 民办教育
        leaseItemDedupTypeParam.put("HEALTHCARE", Arrays.asList("生产线名称", "设备名称", "发票号")); // 医疗健康

        approveStatusMap.put("UNDER_APPROVAL", "审批中");
        approveStatusMap.put("UN_SUBMIT", "未提交");
        approveStatusMap.put("APPROVAL_PASS", "审批通过");
        approveStatusMap.put("APPROVAL_REJECT", "审批拒绝");
        approveStatusMap.put("CANCEL", "已关闭");
        approveStatusMap.put("CANCELED", "已取消");
    }

    public static Map<String, String> fiveClassMap = new HashMap<>();
    static{
        fiveClassMap.put("NORMAL","正常");
        fiveClassMap.put("FOCUS","关注");
        fiveClassMap.put("SECONDARY","次级");
        fiveClassMap.put("SUSPICIOUS","可疑");
        fiveClassMap.put("LOSS","损失");
        fiveClassMap.put("OTHER","未分类");
    }

    public static final String splitLine = "___";

    public static final String MINUS_ONE = "-1";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String ARCHIVES = "ARCHIVES_CQ"; // 需要发送苍穹的附件类型
    public static final String DATA_SHARE_FK = "data_share_fk"; // 费控共享表信息
    public static final String YGBX = "YGBX"; // 员工报销单 单据标记

    public static final String ARCHIVE_STAFF = "ARCHIVE_STAFF"; // 员工报销单
    public static final String ARCHIVE_TRAVEL = "ARCHIVE_TRAVEL"; // 差旅报销单

    public static final String CQSENDSTATUS_UNSEND = "0"; // 苍穹发送状态 未发送
    public static final String CQSENDSTATUS_SENDED = "1"; // 苍穹发送状态 发送成功
    public static final String CQSENDSTATUS_ERROR = "-1"; // 苍穹发送状态 发送失败

    public static final String ER_DAILYREIMBURSEBILL = "er_dailyreimbursebill"; // 苍穹 单据类型 员工报销单
    public static final String ER_TRIPREIMBURSEBILL = "er_tripreimbursebill"; // 苍穹 单据类型 差旅报销单

    public static final String AP_FINAPBILL = "ap_finapbill"; // 苍穹 单据类型 财务应付单
    public static final String AP_PAYAPPLY = "ap_payapply"; // 苍穹 单据类型 付款申请单
    public static final String AR_FINARBILL = "ar_finarbill"; // 苍穹 单据类型 财务应收单
    public static final String FR_GLREIM_PAYBILL = "fr_glreim_paybill"; // 苍穹 单据类型 总账付款申请单
}