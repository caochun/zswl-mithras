package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaseTextFileEnum implements PullDown, IMaterialsTypeConvert {
    /**
     * 租赁物文本类型
     */
    LEASE_ITEM("租赁物清单", 3),
    LEASE_ENTER_LETTER("租赁物确认函", 2);

    public final String display;
    public final Integer order;

    public static LeaseTextFileEnum of(String code) {
        for (LeaseTextFileEnum value : LeaseTextFileEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String businessModule() {
        return "LEASE_TEXT";
    }

    @Override
    public String display() {
        return display;
    }
}
