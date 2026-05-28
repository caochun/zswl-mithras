package cn.zswltech.mithras.service.service.lib.payment.handler;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 09:56
 */
public enum PaymentInfoModule {
    BASE_INFO("付款申请基本信息"),
    PLANED_DETAILS("计划付款明细"),
    POLICE_INFO("保单信息"),
    QUESTIONNAIRE("问卷调查"),
    MATERIALS_LIST("资料清单"),
    ;

    PaymentInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static PaymentInfoModule of(String code) {
        for (PaymentInfoModule value : PaymentInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
