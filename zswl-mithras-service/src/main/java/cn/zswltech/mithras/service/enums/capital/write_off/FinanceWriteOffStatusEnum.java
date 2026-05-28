package cn.zswltech.mithras.service.enums.capital.write_off;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/27 10:47
 * @description 自动核销模块专用核销状态枚举
 */
@Getter
@AllArgsConstructor
public enum FinanceWriteOffStatusEnum {
    NO_WRITE_OFF("未核销"),
    WRITE_OFF("已核销"),
    IGNORE("忽略"),
    ;
    private final String display;

    public static FinanceWriteOffStatusEnum find(String name) {
        for (FinanceWriteOffStatusEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
