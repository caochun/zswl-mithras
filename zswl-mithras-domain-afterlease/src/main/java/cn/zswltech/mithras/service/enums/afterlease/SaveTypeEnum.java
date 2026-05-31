package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2025/1/7 11:05
 * @description
 */
@Getter
@AllArgsConstructor
public enum SaveTypeEnum implements PullDown {
    DRAFT("暂存"),
    SAVE("保存"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
