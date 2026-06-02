package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/3/24/19:35
 * @description
 */
@Getter
@AllArgsConstructor
public enum FluctuationValueEnum implements PullDown {
    /**
     * 波动值类型枚举
     */
    DIRECT("直接取波动值"),
    INDIRECT("根据波动值取映射值"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
