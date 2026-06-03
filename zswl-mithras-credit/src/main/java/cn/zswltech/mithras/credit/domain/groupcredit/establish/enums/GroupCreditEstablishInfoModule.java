package cn.zswltech.mithras.credit.domain.groupcredit.establish.enums;

public enum GroupCreditEstablishInfoModule {

    BASE_INFO("集团授信立项基本信息"),
    MATERIALS_LIST("资料清单"),
    ;

    GroupCreditEstablishInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static GroupCreditEstablishInfoModule of(String code) {
        for (GroupCreditEstablishInfoModule value : GroupCreditEstablishInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
