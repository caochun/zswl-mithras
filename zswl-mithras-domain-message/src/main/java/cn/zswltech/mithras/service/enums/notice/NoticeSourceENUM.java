package cn.zswltech.mithras.service.enums.notice;


import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NoticeSourceENUM implements PullDown {

    APPROVAL_PROCESS("审批流程"),

    PROJ_REVIEW("项目评审-通知法务/风控"),

    ASSET_CLASSIFY("资产分类"),

    KPI_PROJECT_ALLOCATION("绩效考核-项目分配表"),

    KPI_PROJECT_MANAGER_ASSESSMENT("绩效考核-项目经理考评表"),

    PAYMENT("收付款管理"),

    RENT("收租提醒"),

    RENT_RECEIVED("租金到账通知"),

    CREDIT_REPORT("征信报送"),

    OPINION_MONITOR("舆情监控"),

    ARCHIVES("归档提醒"),
    POLICY("维护保单提醒"),
    CLIENT_RELEASE("客户释放通知"),
    PROCESS_PREPARE("待发起"),

    AFTER_LEASE_CHECK("租后检查"),
    RATING_CLIENT_UPDATE("客户评级"),
    RATING_AMOUNT_UPDATE("债项评级"),
    CLIENT_MANAGEMENT_EMAIL_CHANGE("客户管理邮箱信息变更"),
    PROJECT_NO_PROGRESS("项目无进展"),
    BIZ_REMINDER("业务提醒"),
    ACCOUNT_EXPIRE("账户到期提醒"),
    FUND_RECEIPT_REPAY("还本付息提醒"),
    APPRAISAL_COMPANY_WHITELIST_EXPIRE("评估机构白名单准入到期通知")
    ;

    NoticeSourceENUM(String display) {
        this.display = display;
    }

    public final String display;

    private static Map<String, NoticeSourceENUM> map;

    static {
        map = Stream.of(NoticeSourceENUM.values()).collect(Collectors.toMap(NoticeSourceENUM::name, c -> c));
    }

    public static NoticeSourceENUM of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
