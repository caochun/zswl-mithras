package cn.zswltech.mithras.riskcontrol.common;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/8 16:35
 */
public enum RiskOpinionWarnCordType implements PullDown {
    RED("红灯"),
    YELLOW("黄灯"),
    OPINION("舆情"),
    ;
    private String display;

    RiskOpinionWarnCordType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
