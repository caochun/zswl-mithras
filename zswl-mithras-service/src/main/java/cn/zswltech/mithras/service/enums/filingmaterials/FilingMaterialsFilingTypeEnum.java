package cn.zswltech.mithras.service.enums.filingmaterials;


import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description: 资料归档归档类型
 * @author: lllin
 * @date: 2025-12-04
 */
public enum FilingMaterialsFilingTypeEnum implements PullDown {
    BUSINESS_MATERIALS("项目资料"),
    FUND_DIRECT_FINANCING("直融资料"),
    FUND_FINANCING("间融资料"),
    SITE("租后(现场检查)资料"),
    OFFSITE("租后(非现场检查)资料"),
    OTHER("其他资料"),
    ;

    FilingMaterialsFilingTypeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static FilingMaterialsFilingTypeEnum of(String code) {
        for (FilingMaterialsFilingTypeEnum value : FilingMaterialsFilingTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
