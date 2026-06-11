package cn.zswltech.mithras.workbench.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/9 10:34
 */
public enum WorkbenchMetricTimeScope implements PullDown {
    /**
     * 本周
     */
    WEEKLY("本周"),
    /**
     * 本月
     */
    MONTHLY("本月"),
    /**
     * 本季度
     */
    QUARTERLY("本季度"),
    /**
     * 本年
     */
    YEARLY("本年"),
    ;

    private final String display;

    WorkbenchMetricTimeScope(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
