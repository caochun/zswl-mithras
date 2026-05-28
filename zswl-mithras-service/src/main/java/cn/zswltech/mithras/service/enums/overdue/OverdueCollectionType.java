package cn.zswltech.mithras.service.enums.overdue;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 17:00
 */
public enum OverdueCollectionType implements PullDown {
    /**
     * 现场催收
     */
    LIVE("现场催收"),
    /**
     * 非现场催收
     */
    NON_LIVE("非现场催收"),
    /**
     * 发函催收
     */
    SEND_LETTER("发函催收"),
    ;

    private final String display;

    OverdueCollectionType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
