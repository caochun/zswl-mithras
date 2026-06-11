package cn.zswltech.mithras.workbench.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/8 16:20
 */
public enum WorkbenchMetricUnit implements PullDown {
    /**
     * 万元
     */
    WAN("万元"),
    /**
     * 户
     */
    HU("户"),
    /**
     * 个
     */
    GE("个"),
    /**
     * 百分比
     */
    PERCENT("%"),
    ;
    private final String display;

    WorkbenchMetricUnit(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
