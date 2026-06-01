package cn.zswltech.mithras.service.enums;

/**
 *
 * @author: jackerhe
 * 发送给oa地址管理，有app端需补全appUrl，没有只需pcUrl
 **/
public enum MessageUrlEnum {
    PROCESS_END("/process/detail/%s?diff=processInstanceId&auth=oa", "/process/query/detail/%s?typeId=approval&businessKey=%s&diff=processInstanceId&nav=myquery&clientType=%s&flag=info"),  //流程结束
    PROCESS_CC("/process/detail/%s?diff=processInstanceId&auth=oa", "/process/receive/detail/%s?typeId=approval&businessKey=%s&diff=processInstanceId&clientType=%s&flag=info"),  //抄送
    NOTICE_CONTEXT("/process/detail/%s?auth=oa", "/process/receive/detail/%s?typeId=approval&businessKey=%s&diff=taskId&clientType=%s&flag=info"),  //我收到的-审批通知
    MY_APPLY_NOTICE_CONTEXT("/process/detail/%s?diff=processInstanceId&auth=oa", "/process/application/detail/%s?typeId=approval&businessKey=%s&diff=taskId&clientType=%s&flag=info"),  //我发起的-审批通知
    COLLECTION_COMPLETE("","/cpm/collectionWriteOff/detail/%s"),//收款核销完毕
    PAYMENT_COMPLETE("","/cpm/paymentWriteOff/detail/%s"),//付款核销完毕
    SETTLE_PASS("","/contract/list/detail/%s?canEditFlags=false&contractStatus=%s"),
    CLIENT_NEW("", "/customer/maintain/detail/%d?clientType=%s"),//新建客户
    ASSET_CLASSIFY_REMIND("", "/risk/level5Classify"),
    CREDIT_REPORT("", "/creditTable/wait"),
    KPI_PROJECT_ALLOCATION_LIST("", "/kpi/projectAllot"),
    KPI_PROJECT_ASSESSMENT_LIST("", "/kpi/pmAssess"),
    INDICATOR_WARNING("", "/risk/riskStrategy/indicatorManage/detail/%s"),
    ARCHIVES("", "/archives/manage/%s"),
    POLICY("", "/afterLease/policyManage/detail/%s?source=maintenance"),
    BILL_PAY("", "/cpm/paymentWriteOff/detail/%s"),
    BILL_COLLECTION("", "/cpm/collectionWriteOff/detail/%s"),
    REVIEW_NOTICE("", "/project/review"),
    NOTICE_TREASURER_REPORT("", "/baseData/financeSheet"),
    CLIENT_PLAN_CHECK("", "/afterLease/checkPlan/template/%s"),
    CLIENT_RELEASE("", "/project/establishment/detail/%s"),
    CLIENT_RELEASE_REMIND_ESTABLISH("","/project/establishment/detail/%s"),
    CLIENT_RELEASE_REMIND_REVIEW("","/project/review/detail/%s"),
    AFTER_LEASE_CHECK_CHANGE("", "/afterLease/checkPlan/template/%s"),
    PROCESS_PREPARE("", "/process/application/detail/%s?tab=prepare&typeId=approval"),
    APPRAISAL_COMPANY_WHITELIST_EXPIRE("", "/whiteList/detail/%s")
    ;

    MessageUrlEnum(String appUrl, String pcUrl) {
        this.appUrl = appUrl;
        this.pcUrl = pcUrl;
    }

    public final String appUrl;

    public final String pcUrl;

    public static MessageUrlEnum of(String code) {
        for (MessageUrlEnum value : MessageUrlEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
