package cn.zswltech.mithras.service.enums;

/**
 * @author luyi
 */
public enum InfoModule {
    CORP_COMMERCE("法人工商信息"), CORP_ADDRESS("法人地址信息"), CORP_CONTACT("法人企业联系人"), CORP_BOND("法人发债及评级信息"),
    CORP_SHAREHOLDER("法人股东信息"), CORP_RELATED_ENTERPRISE("法人关联企业"), CORP_BANK_ACCOUNT("法人银行账户"),
    NORMAL_BASE("自然人基本信息"), NORMAL_BANK_ACCOUNT("自然人银行账户"), NORMAL_SPOUSE("自然人配偶信息"),
    CORP_MATERIALS_LIST("资料清单"),NORMAL_MATERIALS_LIST("资料清单");

    InfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static InfoModule of(String code) {
        for (InfoModule value : InfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
