package cn.zswltech.mithras.kpi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/7/1/14:07
 * @description
 */
@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {
    PLATFORM("公用事业类"),
    PUBLIC("公共事业"),
    INDUSTRY("其他产业类"),
    FTP_CIVIL_CONSUMPTION("民生消费类"),
    FTP_STATE_OWNED_INDUSTRY("国有产业类"),
    TOTAL("合计"),
    DEPT_TOTAL("部门合计"),
    COMPANY_TOTAL("公司合计"),
    ALL_TOTAL("总合计"),
    ;


    private final String display;

    public static BusinessTypeEnum getByDisplay(String display) {
        for (BusinessTypeEnum businessTypeEnum : BusinessTypeEnum.values()) {
            if (businessTypeEnum.getDisplay().equals(display)) {
                return businessTypeEnum;
            }
        }
        return null;
    }

    public static BusinessTypeEnum ofName(String name) {
        for (BusinessTypeEnum businessTypeEnum : BusinessTypeEnum.values()) {
            if (businessTypeEnum.name().equals(name)) {
                return businessTypeEnum;
            }
        }
        return null;
    }
}
