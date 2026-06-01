package cn.zswltech.mithras.service.enums.overdue;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 09:18
 */
public enum LitigationStatus implements PullDown {
    /**
     * 诉前准备
     */
    PREPARE("诉前准备"),
    /**
     * 诉讼中
     */
    LITIGATION("诉讼中"),
    /**
     * 执行中
     */
    EXECUTION("执行中"),
    /**
     * 执行终本
     */
    EXECUTION_END("执行终本"),
    /**
     * 已结案
     */
    END("已结案")
    ;

    private final String display;
    LitigationStatus(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
