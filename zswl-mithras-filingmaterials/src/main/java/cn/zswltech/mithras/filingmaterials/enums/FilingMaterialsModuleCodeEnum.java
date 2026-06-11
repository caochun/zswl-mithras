package cn.zswltech.mithras.filingmaterials.enums;


/**
 * @description: 资料归档审批状态
 * @author: lllin
 * @date: 2025-12-04
 */
public enum FilingMaterialsModuleCodeEnum {
    REFERENCE_MATERIALS("参考"),
    OPERATIONAL_REVIEW("归档");

    FilingMaterialsModuleCodeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static String getModuleName(String code) {
        for (FilingMaterialsModuleCodeEnum value : FilingMaterialsModuleCodeEnum.values()) {
            if (value.name().equals(code)) {
                return value.display;
            }
        }
        return null;
    }

}
