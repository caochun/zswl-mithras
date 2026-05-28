package cn.zswltech.mithras.service.enums.kpi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/7/1/14:21
 * @description
 */
@Getter
@AllArgsConstructor
public enum BelongTypeEnum {

    PERSONAL("个人"),
    COMPANY("公司"),
    DEPARTMENT("部门");

    private final String display;

    public static BelongTypeEnum ofName(String name) {
        for (BelongTypeEnum anEnum : BelongTypeEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }


}
