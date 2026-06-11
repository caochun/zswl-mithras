package cn.zswltech.mithras.riskcontrol.common;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/8 16:41
 */
public enum MetricCategory implements PullDown {
    /**
     * 控制类
     */
    CONTROL("控制类"),
    /**
     * 指导类
     */
    GUIDANCE("指导类"),
    ;
    private String display;

    MetricCategory(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
