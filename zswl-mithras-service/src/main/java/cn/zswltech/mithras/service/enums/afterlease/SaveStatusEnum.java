package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2025/1/7 11:05
 * @description
 */
@Getter
@AllArgsConstructor
public enum SaveStatusEnum implements PullDown {
    NO_SAVE("未保存"),
    SAVED("已保存"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
