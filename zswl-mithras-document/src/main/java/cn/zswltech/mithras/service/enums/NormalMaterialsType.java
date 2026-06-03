package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;

/**
 * @author luyi
 */
@AllArgsConstructor
public enum NormalMaterialsType implements PullDown, IMaterialsTypeConvert {
    ID_CARD("身份证", "身份证", 1),
    HOUSEHOLD("户口本", "户口本", 2),
    MARRIAGE_CERT("结婚证", "结婚证", 3),
    PERSONAL_CREDIT_REPORT("个人信用报告", "信用", 4),
    NAMED_ASSETS("名下资产", "资产", 5),
    OTHERS("其他", "", 6);

    public final String display;
    public final String keyword;
    public final Integer order;

    public static NormalMaterialsType of(String code) {
        for (NormalMaterialsType value : NormalMaterialsType.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String businessModule() {
        return "CLIENT";
    }

    @Override
    public String display() {
        return display;
    }
}
