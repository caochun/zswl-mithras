package cn.zswltech.mithras.foundation.thirdparty;


import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import lombok.Getter;

import java.util.List;

/**
 * 三方接口枚举
 *
 * @author wang
 * @date 2022/4/11 7:10 PM
 */
@Getter
public enum PlatformApiEnum {

    // 天眼查
    TYC_HOLDER(1L, "tycHolder", "企业股东信息", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_MORTGAGE_INFO(2L, "tycMortgageInfo", "动产抵押", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_EQUITY_INFO(3L, "tycEquityInfo", "股权出质", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_PUNISHMENT_INFO(4L, "tycPunishmentInfo", "行政处罚", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_ABNORMAL(5L, "tycAbnormal", "经营异常", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_JUDICIAL(6L, "tycJudicial", "司法协助", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_LAW_SUIT(7L, "tycLawSuit", "法律诉讼", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_CONSUMPTION_RESTRICTION(8L, "tycConsumptionRestriction", "限制消费令", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_ZHIXING_INFO(9L, "tycZhixingInfo", "被执行人", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_DISHONEST(10L, "tycDishonest", "失信人", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    TYC_LAW_SUIT_DETAIL(11L, "tycLawSuitDetail", "法律诉讼详情", "tyc", RequestModeEnum.GET, EncryptionModeEnum.NONE),

    ALI_OCR(101L, "aliOcr", "阿里云ocr识别", "aliyun", RequestModeEnum.POST, EncryptionModeEnum.NONE),

    //对接苍穹
    CQ_RECEIVE(201L, "cqReview", "苍穹应收单", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ_APP_TOKEN(202L, "cqAppToken", "苍穹获取appToken", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ_ACCESS_TOKEN(203L, "cqAccessToken", "苍穹获取accessToken", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ_PAYMENT(204L, "cqPayment", "苍穹生成应付单", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ_REFUND(205L, "cqRefund", "苍穹退款单", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ_WITHDRAW(206L, "cqWithdraw", "苍穹撤回数据", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ_RENT_RECEIVE(207L, "cqRentReview", "苍穹租金应收单", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ_BILL_PAYMENT(208L, "cqApplyBillPayment", "苍穹付款申请", "cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    //同步拨备数据
    CQ_RECEIVE_PROVISION(209L, "cqReceiveBillPayment", "苍穹接收拨备信息","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    //通用应收单_新增
    CQ2_PLAN_COLLECTION(210L, "cq2PlanCollection", "苍穹通用应收单","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_COLLECTION(211L, "cq2Collection", "苍穹收款单","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_PAYMENT(212L,"cq2Payment","苍穹付款申请单","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_ACCOUNT_APPLICATION(213L,"cq2AccountApplication","苍穹记账申请单","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_FLOW_QUERY(214L,"cq2FlowQuery","苍穹流水查询接口","cq", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    CQ2_WITHDRAW(215L,"cq2Withdraw","苍穹删除接口","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_ACCOUNT_AGE_ADD(216L,"cq2AccountAgeAdd","苍穹帐龄新增接口","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_ACCOUNT_AGE_MODIFY(216L,"cq2AccountAgeModify","苍穹帐龄修改接口","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_ACCOUNT_AGE_DELETE(216L,"cq2AccountAgeDelete","苍穹帐龄删除接口","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CQ2_ATTACHMENT_SAVE(217L,"cq2AttachmentSave","苍穹外部系统附件补充","cq", RequestModeEnum.POST, EncryptionModeEnum.NONE),

    //舆情相关接口
    PO_REGISTER(301L, "poRegister", "舆情管理-监测客户注册", "po",RequestModeEnum.POST, EncryptionModeEnum.NONE),
    PO_LIST(302L, "poList", "舆情管理-舆情信息查询", "po",RequestModeEnum.POST, EncryptionModeEnum.NONE),

    // 金控接口
    JK_REPORT_BCM_JINKONG_BALANCE_MF(401L, "jkReportBcmBalanceMf", "科目余额表主表", "jk", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    JK_REPORT_BCM_JINKONG_FFLEXFILED_ASSIST_MF(402L, "jkReportBcmFflexfiledAssistMf", "辅助核算维度表", "jk", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    JK_REPORT_BCM_JINKONG_ASSET_MF(403L, "jkReportBcmAssetMf", "月报-资产负债表", "jk", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    JK_REPORT_BCM_JINKONG_PROFIT_MF(404L, "jkReportBcmProfitMf", "月报-利润表", "jk", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    JK_REPORT_BCM_JINKONG_CASHFLOW_MF(405L, "jkReportBcmCashflowMf", "月报-现金流量表", "jk", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    JK_BLACK_GRAY_COLLISION_LIBRARY(406L, "jkBlackGrayCollisionLibrary", "黑灰名单撞库查询", "jk", RequestModeEnum.POST, EncryptionModeEnum.NONE),

    //保融接口
    BR_FLOW_QUERY(501L,"brFlowQuery","保融流水查询接口","br", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    // 以后每个三方的id往上+100，比如对接企微第一个接口，id=101

    //交投oa
    CICO_OA_DATA_LIST(601L, "CicoOADataList", "交投oa代办列表", "CicoOA", RequestModeEnum.GET, EncryptionModeEnum.NONE),
    CICO_OA_FLOW_OPERATE_LIST(602L, "CicoOAFlowOperateList", "交投oa通知操作列表", "CicoOA", RequestModeEnum.POST, EncryptionModeEnum.NONE),

    //云湖接口
    YUNHU_REPORT_BCM_JINKONG_BALANCE_MF(701L, "yunhuReportBcmBalanceMf", "云湖科目余额表主表", "yunhu", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    YUNHU_REPORT_BCM_JINKONG_FFLEXFILED_ASSIST_MF(702L, "yunhuReportBcmFflexfiledAssistMf", "云湖辅助核算维度表", "yunhu", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    YUNHU_REPORT_BCM_JINKONG_ASSET_MF(703L, "yunhuReportBcmAssetMf", "云湖月报-资产负债表", "yunhu", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    YUNHU_REPORT_BCM_JINKONG_PROFIT_MF(704L, "yunhuReportBcmProfitMf", "云湖月报-利润表", "yunhu", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    YUNHU_REPORT_BCM_JINKONG_CASHFLOW_MF(705L, "yunhuReportBcmCashflowMf", "云湖月报-现金流量表", "yunhu", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    YUNHU_REPORT_INDICATOR_DATA(706L, "yunhuReportIndicatorData", "云湖月报-国资快报", "yunhu", RequestModeEnum.POST, EncryptionModeEnum.NONE),

    //逾期报送
    OVERDUE_REPORT_APP_TOKEN(801L, "overdueReportAppToken", "逾期报送获取appToken", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    OVERDUE_REPORT_ACCESS_TOKEN(802L, "overdueReportAccessToken", "逾期报送获取accessToken", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    OVERDUE_REPORT_BAT_SAVE(803L, "overdueReportBatSave", "应收逾期集成保存接口", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    OVERDUE_REPORT_SET_BAT_SAVE(804L, "overdueReportSetBatSave", "应收逾期结算保存接口", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    OVERDUE_REPORT_UNAUDIT(805L, "overdueReportUnaudit", "应收逾期反审核接口", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    OVERDUE_REPORT_DEL(806L, "overdueReportDel", "应收逾期删除接口", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    OVERDUE_REPORT_REAPP(807L, "overdueReportReapp", "应收逾期结算反审核接口", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    OVERDUE_REPORT_SET_DEL(808L, "overdueReportSetDel", "应收逾期结算删除接口", "overdueReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),

    //征信解析
    CREDIT_REPORT_QUERY_ENT(901L, "creditReportQueryEntFourEleAuth", "征信解析-新增档案信息", "creditReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CREDIT_REPORT_QUERY_REPORT(902L, "creditReportQueryReport", "征信解析-单笔查询请求", "creditReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CREDIT_REPORT_OBTAIN_JSON(903L, "creditReportObtainResultJSON", "征信解析-单笔查询JSON结果", "creditReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),
    CREDIT_REPORT_OBTAIN_PDF(904L, "creditReportObtainResultPDF", "征信解析-单笔查询PDF结果", "creditReport", RequestModeEnum.POST, EncryptionModeEnum.NONE),

    ;

    PlatformApiEnum(Long id, String apiCode, String apiName, String ownerPlatformName, RequestModeEnum requestModeEnum, EncryptionModeEnum encryptionModeEnum) {
        this.id = id;
        this.apiName = apiName;
        this.apiCode = apiCode;
        this.ownerPlatformName = ownerPlatformName;
        this.requestModeEnum = requestModeEnum;
        this.encryptionModeEnum = encryptionModeEnum;
    }

    public static PlatformApiEnum of(String code) {
        for (PlatformApiEnum value : PlatformApiEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    //苍穹需要手动推送请求
    public static List<PlatformApiEnum> getCqNeedManualApiList() {
        return ListUtil.toList(CQ2_PLAN_COLLECTION, CQ2_COLLECTION, CQ2_PAYMENT, CQ2_ACCOUNT_APPLICATION);
    }

    /**
     * API id
     */
    public final Long id;
    /**
     * apiCode
     */
    public final String apiCode;

    /**
     * api名称
     */
    public final String apiName;

    /**
     * 所属平台
     */
    public final String ownerPlatformName;
    /**
     * 请求方式
     *
     */
    public final RequestModeEnum requestModeEnum;
    /**
     * 加密方式
     *
     */
    public final EncryptionModeEnum encryptionModeEnum;


    public static PlatformApiEnum getPlatformApiByApiCode(String apiCode, String ownerPlatformName) {
        for (PlatformApiEnum platformApiEnum : PlatformApiEnum.values()) {
            if (platformApiEnum.apiCode.equals(apiCode) && platformApiEnum.ownerPlatformName.equals(ownerPlatformName)) {
                return platformApiEnum;
            }
        }
        return null;
    }

    public static String getWithdrawType(PlatformApiEnum platformApiEnum) {
        switch (platformApiEnum) {
            case CQ2_PLAN_COLLECTION:
                return FinancialConstants.YSD;
            case CQ2_COLLECTION:
                return FinancialConstants.SKD;
            case CQ2_PAYMENT:
                return FinancialConstants.FKSQD;
            case CQ2_ACCOUNT_APPLICATION:
                return FinancialConstants.JZSQD;
        }
        return null;
    }
}
