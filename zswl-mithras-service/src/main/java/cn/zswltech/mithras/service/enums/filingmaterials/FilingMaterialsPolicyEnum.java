package cn.zswltech.mithras.service.enums.filingmaterials;


import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description: 资料归档发起方式
 * @author: lllin
 * @date: 2025-12-04
 */
public enum FilingMaterialsPolicyEnum{
    
    POLICY("保单信息");

    FilingMaterialsPolicyEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static FilingMaterialsPolicyEnum of(String code) {
        for (FilingMaterialsPolicyEnum value : FilingMaterialsPolicyEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
