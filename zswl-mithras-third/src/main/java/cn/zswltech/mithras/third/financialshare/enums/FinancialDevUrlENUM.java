package cn.zswltech.mithras.third.financialshare.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName FinancialUrlENUM
 * @Description
 * @Author jackerhe
 * @Date 2022/10/21 10:40 上午
 * @Version 1.0
 **/
public enum FinancialDevUrlENUM {

    APP_TOKEN_INFO("/ierp/api/getAppToken.do", ""),
    ACCESS_TOKEN_INFO("/ierp/api/login.do", ""),
    RECEIVVER_INFO("/ierp/kapi/app/cas/RZYFinArBillSave", ""),
    RECEIVVER_RENT_INFO("/ierp/kapi/app/cas/RZYRentTable", ""),
    PAYMENT_INFO("/ierp/kapi/app/ap/RZYFinApBillSave", ""),
    PAYMENT_BILL_INFO("/ierp/kapi/app/cas/RZYPayApplyBillSave", ""),
    REFUND_INFO("/ierp/kapi/app/cas/RZYPayApplyBillSave", ""),
    WITHDRAW_INFO("/ierp/kapi/app/ap/RZYBackBill", ""),
    CQ_RECEIVE_PROVISION("/ierp/kapi/app/fr/RZYTallyApplyBillWebApiPlugin", ""),
    CQ2_PLAN_COLLECTION("/ierp/kapi/v2/cico/ar/ar_finarbill/save_common_ar", ""),
    CQ2_COLLECTION("/ierp/kapi/v2/cico/cas/cas_recbill/skclnew", ""),
    CQ2_PAYMENT("/ierp/kapi/v2/cico/ap/ap_payapply/RZYPayApplyAPI", ""),
    CQ2_ACCOUNT_APPLICATION("/ierp/kapi/v2/cico/fr/ssc_tallyapplybill/add_ssc_tallyapplybi", ""),
    CQ2_FLOW_QUERY("/ierp/kapi/v2/cico/bei/bei_transdetail/bei_transdetail_query_rzy", ""),
    CQ2_WITHDRAW("/ierp/kapi/v2/cico/ap/businessDeleteApiHandle", ""),
    CQ2_ACCOUNT_AGE_ADD("/ierp/kapi/v2/cico/ar/cico_bus_account_bill/batchsave", ""),
    CQ2_ACCOUNT_AGE_MODIFY("/ierp/kapi/v2/cico/ar/cico_bus_account_bill/update", ""),
    CQ2_ACCOUNT_AGE_DELETE("/ierp/kapi/v2/cico/ar/cico_bus_account_bill/delete", ""),
    BR_FLOW_QUERY("/opr-http/cwgscommon/cwgsapi/cwgsApiInfo", "com.cncico.esb.opr.cwgscommon.cwgsApiInfo.cwgsapi"),
//    CQ2_PLAN_COLLECTION("/v2/cico/ar/ar_finarbill/save_common_ar", ""),
//    CQ2_COLLECTION("/v2/cico/cas/cas_recbill/skclnew", ""),
//    CQ2_PAYMENT("/v2/cico/ap/ap_payapply/payapplysave", ""),
//    CQ2_ACCOUNT_APPLICATION("/v2/cico/fr/ssc_tallyapplybill/add_cico_ssc_tallyapplybi", ""),
//    CQ2_FLOW_QUERY("/ierp/kapi/v2/cico/bei/bei_transdetail/bei_transdetail_query_rzy", ""),

    CQ2_ATTACHMENT_SAVE("/ierp/kapi/v2/cico/frame/cico_exsys_attachment/save", "com.cncico.esb.fin.fss.cico_exsys_attachment.save"), // 外部系统附件补充
    ;
    FinancialDevUrlENUM(String url, String operate) {
        this.url = url;
        this.operate = operate;
    }

    public final String url;


    public final String operate;

    private static Map<String, FinancialDevUrlENUM> map;

    static {
        map = Stream.of(FinancialDevUrlENUM.values()).collect(Collectors.toMap(FinancialDevUrlENUM::name, c -> c));
    }

    public static FinancialDevUrlENUM of (String name) {
        return map.get(name);
    }

}
