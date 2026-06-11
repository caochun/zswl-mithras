package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/9/9 09:33
 **/
@Getter
@AllArgsConstructor
public enum AfterLeaseDeadlineLabelEnum implements PullDown {
    /**
     * 租后检查截止日期标签枚举
     */
    SYSTEM_CALCULATE("系统计算"),
    ASSERT_MANAGER_CONFIRM("资管确认"),
    FIRST_CHECK("初次检查"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
