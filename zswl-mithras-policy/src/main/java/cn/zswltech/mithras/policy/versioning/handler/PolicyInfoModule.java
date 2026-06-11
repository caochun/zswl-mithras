package cn.zswltech.mithras.policy.versioning.handler;



public enum PolicyInfoModule {
    BASE_INFO("保单基本信息"),
    MATERIALS_LIST("资料清单"),
    ;

    PolicyInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static PolicyInfoModule of(String code) {
        for (PolicyInfoModule value : PolicyInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
