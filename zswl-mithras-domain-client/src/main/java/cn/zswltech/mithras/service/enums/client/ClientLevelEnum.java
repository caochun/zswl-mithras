package cn.zswltech.mithras.service.enums.client;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/6/6
 * @description
 */
@Getter
@AllArgsConstructor
public enum ClientLevelEnum implements PullDown {
    VIEW("查看权", 1),
    APPLY("申办权", 2),
    MANAGE("管护权", 3)
    ;

    private final String display;
    private final int level;

    @Override
    public String display() {
        return this.display;
    }

    ClientLevelEnum(String display, Integer level) {
        this.display = display;
        this.level = level;
    }


    public static ClientLevelEnum of(String name) {
        for (ClientLevelEnum value : ClientLevelEnum.values()) {
            if (StrUtil.isNotEmpty(name) && name.contains(value.display)) {
                return value;
            }
        }
        return null;
    }

    public static ClientLevelEnum findByLevel(Integer level) {
        for (ClientLevelEnum value : ClientLevelEnum.values()) {
            if (Objects.equals(value.getLevel(), level)) {
                return value;
            }
        }
        return null;
    }

}
