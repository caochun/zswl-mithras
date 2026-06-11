package cn.zswltech.mithras.filingmaterials.enums;


/**
 * @description: 资料归档发起方式
 * @author: lllin
 * @date: 2025-12-04
 */
public enum FilingMaterialsInitiationMethodEnum {
    MANUAL("手工发起"),
    SYSTEM("系统发起");

    FilingMaterialsInitiationMethodEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static FilingMaterialsInitiationMethodEnum of(String code) {
        for (FilingMaterialsInitiationMethodEnum value : FilingMaterialsInitiationMethodEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
