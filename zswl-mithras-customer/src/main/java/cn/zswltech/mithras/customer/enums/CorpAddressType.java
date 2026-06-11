package cn.zswltech.mithras.customer.enums;

/**
 * @author luyi
 */
public enum CorpAddressType{
    REGISTRY_ADDRESS("注册地址"), WORK_ADDRESS("办公地址");

    CorpAddressType(String display) {
        this.display = display;
    }

    public final String display;

    public static CorpAddressType of(String name) {
        for (CorpAddressType value : CorpAddressType.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
