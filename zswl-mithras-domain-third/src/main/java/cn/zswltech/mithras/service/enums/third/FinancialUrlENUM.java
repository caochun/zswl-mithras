package cn.zswltech.mithras.service.enums.third;

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
public enum FinancialUrlENUM {

   /* APP_TOKEN_INFO("/ierp/api/getAppToken.do", ""),
    ACCESS_TOKEN_INFO("/ierp/api/login.do", ""),
    RECEIVVER_INFO("/ierp/kapi/app/cas/RZYFinArBillSave", ""),
    RECEIVVER_RENT_INFO("/ierp/kapi/app/cas/RZYRentTable", ""),
    PAYMENT_INFO("/ierp/kapi/app/ap/RZYFinApBillSave", ""),
    PAYMENT_BILL_INFO("/ierp/kapi/app/cas/RZYPayApplyBillSave", ""),
    REFUND_INFO("/ierp/kapi/app/cas/RZYPayApplyBillSave", ""),
    WITHDRAW_INFO("/ierp/kapi/app/ap/RZYBackBill", ""),
    CQ_RECEIVE_PROVISION("/ierp/kapi/app/fr/RZYTallyApplyBillWebApiPlugin", ""),
*/

    APP_TOKEN_INFO("/fin-http/fss/ierp/getAppToken", "com.cncico.esb.fin.fss.getAppToken.ierp"),

    ACCESS_TOKEN_INFO("/fin-http/fss/ierp/login", "com.cncico.esb.fin.fss.login.ierp"),


    /*----------------------------------------------20251209 确认为1期接口，未实际推送苍穹 start -------------------------------------------------------------*/
    RECEIVVER_INFO("/fin-http/fss/ierp/RZYFinArBillSave", "com.cncico.esb.fin.fss.RZYFinArBillSave.ierp"),


    RECEIVVER_RENT_INFO("/fin-http/fss/ierp/RZYRentTable", "com.cncico.esb.fin.fss.RZYRentTable.ierp"),

    PAYMENT_INFO("/fin-http/fss/ierp/RZYFinApBillSave", "com.cncico.esb.fin.fss.RZYFinApBillSave.ierp"),

    PAYMENT_BILL_INFO("/fin-http/fss/ierp/RZYPayApplyBillSave", "com.cncico.esb.fin.fss.RZYPayApplyBillSave.ierp"),

    REFUND_INFO("/fin-http/fss/ierp/RZYPayApplyBillSave", "com.cncico.esb.fin.fss.RZYPayApplyBillSave.ierp"),

    WITHDRAW_INFO("/fin-http/fss/ierp/RZYBackBill", "com.cncico.esb.fin.fss.RZYBackBill.ierp"),

    CQ_RECEIVE_PROVISION("/fin-http/fss/ierp/RZYTallyApplyBillWebApiPlugin", "com.cncico.esb.fin.fss.RZYTallyApplyBillWebApiPlugin.ierp"),
    /*----------------------------------------------20251209 确认为1期接口，未实际推送苍穹 start -------------------------------------------------------------*/

    //二期
    CQ2_PLAN_COLLECTION("/fin-http/fss/ierp/save_common_ar", "com.cncico.esb.fin.fss.save_common_ar.ierp"),

    CQ2_COLLECTION("/fin-http/fss/ierp/skclnew", "com.cncico.esb.fin.fss.skclnew.ierp"),

    CQ2_PAYMENT("/fin-http/fss/ierp/RZYPayApplyAPI", "com.cncico.esb.fin.fss.RZYPayApplyAPI.ierp"),

    CQ2_ACCOUNT_APPLICATION("/fin-http/fss/ierp/add_ssc_tallyapplybi", "com.cncico.esb.fin.fss.add_cico_ssc_tallyapplybi.ierp"),

    CQ2_FLOW_QUERY("/fin-http/fss/ierp/bei_transdetail_query_rzy", "com.cncico.esb.fin.fss.bei_transdetail_query_rzy.ierp"),

    CQ2_WITHDRAW("/fin-http/fss/ierp/businessDeleteApiHandle", "com.cncico.esb.fin.fss.businessDeleteApiHandle.ierp"),

    BR_FLOW_QUERY("/opr-http/cwgscommon/cwgsapi/cwgsApiInfo", "com.cncico.esb.opr.cwgscommon.cwgsApiInfo.cwgsapi"),

    CQ2_ACCOUNT_AGE_ADD("/fin-http/fss/cico_bus_account_bill/batchsave", "com.cncico.esb.fin.fss.billbatchsave.cico_bus_account_bill"),
    CQ2_ACCOUNT_AGE_MODIFY("/fin-http/fss/cico_bus_account_bill/update", "com.cncico.esb.fin.fss.billupdate.cico_bus_account_bill"),
    CQ2_ACCOUNT_AGE_DELETE("/fin-http/fss/cico_bus_account_bill/delete", "com.cncico.esb.fin.fss.billdelete.cico_bus_account_bill"),
    CQ2_ATTACHMENT_SAVE("/fin-http/fss/cico_exsys_attachment/save", "com.cncico.esb.fin.fss.cico_exsys_attachment.save"),

    ;
    FinancialUrlENUM(String url, String operate) {
        this.url = url;
        this.operate = operate;
    }

    public final String url;


    public final String operate;

    private static Map<String, FinancialUrlENUM> map;

    static {
        map = Stream.of(FinancialUrlENUM.values()).collect(Collectors.toMap(FinancialUrlENUM::name, c -> c));
    }

    public static FinancialUrlENUM of (String name) {
        return map.get(name);
    }

}
