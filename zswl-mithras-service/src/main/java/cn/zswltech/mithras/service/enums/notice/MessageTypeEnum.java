package cn.zswltech.mithras.service.enums.notice;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 维护消息通知类型
 * display 通知中文名称，用于页面展示搜索，display不可重复
 * message 统一后缀，用于维护消息通知风格，使其尽量保持统一
 **/
public enum MessageTypeEnum implements PullDown {

    UNDER_APPROVAL("审批待办", "。"),
    APPROVAL_PASS("审批通过", "。"),
    APPROVAL_REJECT("审批拒绝", "。"),
    CANCELED("审批取消", "。"),
    CC("抄送通知", "。"),

    REMIND("业务提醒", "即将进入项目评审阶段。"),

    CLIENT_NEW("客户新增", "请尽快前往集团客商系统创建该客户！"),
    PROJ_REVIEW_AUDITED("项目审核", "。"),

    COLLECTION("收款核销", "完成一笔收款。"),
    PAYMENT("付款核销", "完成一笔付款。"),
    SETTLE("结清通知", "的收款已全部核销完毕"),

    RENT("收租提醒", ",请关注"),

    START_RENT("起租提醒", "。"),

    RENT_RECEIVED("租金到账通知", "。"),
    COLLECTION_NOTICE("核销提醒", "。"),

    BASE_DATA_LPR_REMIND("LPR数据维护", "。"),

    CREDIT_REPORT_DATA_CHANGE("征信报送", "。"),

    KPI_PROJECT_ALLOCATION_REMIND("绩效考核-项目分配表填写提醒", "。"),

    KPI_PROJECT_MANAGER_ASSESSMENT_REMIND("绩效考核-项目经理考评表填写提醒", "。"),
    INDICATOR_WARNING("指标预警", "，请尽快查看。"),
    OPINION_MONITOR("舆情监控", "请尽快查看。"),
    ARCHIVES("归档提醒", "尽快进行归档"),
    POLICY("维护保单提醒", "尽快维护保单信息"),
    BILL("票据维护提醒", "请维护票据信息."),
    PROJ_ESTABLISH_REVIEW("评审提醒", "已立项3个月，但未发起评审，请确认是否需手工关闭立项。"),
    REMINDER_NOTICE("催办通知", "。"),
    CLIENT_PLAN_CHECK_SPONSOR("租后检查提醒", ""),
    CLIENT_PLAN_CHECK_RISK_MANAGER("租后检查提醒", "仍未提交租后检查报告，请及时关注"),
    CLIENT_RELEASE("客户释放通知", ""),

    RENT_PAYMENT_NOTIFY("租金支付通知", ""),
    FINANCIAL_ANTI_SETTLEMENT_NOTICE("财务反结算通知", "。"),
    AFTER_LEASE_CHECK_CHANGE("租后管理计划调整", "请前往查看确认."),
    PROCESS_PREPARE("待发起提醒", ",请及时前往待发起确认。"),
    AFTER_LEASE_CHECK_UPDATE("租后检查计划修改", ""),
    AFTER_LEASE_CHECK_CLOSE("租后检查计划关闭", ""),
    CLIENT_MANAGEMENT_EMAIL_CHANGE_NOTICE("客户管理个人信息邮箱变更通知","客户管理个人信息邮箱变更通知"),
    PROCESS_AMC_CONFIRM("资产管理岗待确认提醒", ",请及时前往待发起确认。"),
    //AFTER_LEASE_CHECK_STRATEGY_CHANGE("租后检查资产管理策略调整", "请前往查看确认.")

    RATING_CLIENT_OVER_DUE("客户评级即将过期", "请前往查看更新"),
    RATING_AMOUNT_OVER_DUE("债项评级即将过期", "请前往查看更新"),

    PROJECT_NO_PROGRESS("项目无进展", "，请注意!"),
    PROJECT_SETTLE("项目即将结清", ""),
    AFTER_LEASE_CHECK("租后检查提醒", ""),
    ACCOUNT_EXPIRE("账户维护提醒", ""),
    FUND_RECEIPT_REPAY("还本付息提醒", ""),
    APPRAISAL_COMPANY_WHITELIST_EXPIRE("到期通知", "")
    ;

    MessageTypeEnum(String display, String message) {
        this.display = display;
        this.message = message;
    }

    public final String display;

    public final String message;

    private static Map<String, MessageTypeEnum> map;
    private static Set<MessageTypeEnum> ignoreSet;

    static {
        map = Stream.of(MessageTypeEnum.values()).collect(Collectors.toMap(MessageTypeEnum::name, c -> c));
        ignoreSet = new HashSet<>();
        ignoreSet.add(OPINION_MONITOR);
    }

    public static MessageTypeEnum of(String name) {
        return map.get(name);
    }

    public static Boolean isIgnore(MessageTypeEnum messageTypeEnum) {
        return ignoreSet.contains(messageTypeEnum);
    }

    @Override
    public String display() {
        return display;
    }
}
