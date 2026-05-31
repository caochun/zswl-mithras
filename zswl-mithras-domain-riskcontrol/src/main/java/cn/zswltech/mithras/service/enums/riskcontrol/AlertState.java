package cn.zswltech.mithras.service.enums.riskcontrol;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/3 14:55
 */
public enum AlertState implements PullDown {
    /**
     * 正常
     */
    NORMAL("正常"),
    /**
     * 预警
     */
    WARNING("预警"),
    /**
     * 超限
     */
    OVER("超限");
    ;
    private final String display;
    AlertState(String display) {
        this.display = display;
    }
    @Override
    public String display() {
        return display;
    }
}
