package cn.zswltech.mithras.service.enums.projestablish;

public enum ProjEstablishInfoModule {

    BASE_INFO("立项基本信息"),
    ZL_PRICE("租赁报价方案"),
    BL_PRICE("保理报价方案"),
    ZR_PRICE("债券转让报价方案"),
    MATERIALS_LIST("资料清单")
    ;

    ProjEstablishInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static ProjEstablishInfoModule of(String code) {
        for (ProjEstablishInfoModule value : ProjEstablishInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
