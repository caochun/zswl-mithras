package cn.zswltech.mithras.leaseholdproperty.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租赁物经销商模板种类枚举
 *
 * @author yangxiong
 * @since 2023-09-28
 */
@Getter
@AllArgsConstructor
public enum LeaseDealerTemplateTypeEnum {
    /**
     * 车辆
     * 维修设备
     * 钢结构
     * 电子设备
     * 办公家具
     */
    CAR("车辆"),
    MAINTENANCE_EQUIPMENT("维修设备"),
    STEEL_STRUCTURE("钢结构"),
    ELECTRONIC_EQUIPMENT("电子设备"),
    OFFICE_FURNITURE("办公家具");

    private final String display;

    public static LeaseDealerTemplateTypeEnum display(String str) {
        for (LeaseDealerTemplateTypeEnum one : LeaseDealerTemplateTypeEnum.values()) {
            if (one.display.equals(str)) {
                return one;
            }
        }
        return null;
    }
}
