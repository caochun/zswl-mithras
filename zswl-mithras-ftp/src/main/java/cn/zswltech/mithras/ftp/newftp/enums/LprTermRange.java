package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description: 通用的期项范围枚举类
 * @author: zhaozhengkang
 * @date: 2023/5/19 11:16
 */
public enum LprTermRange implements PullDown {
    /**
     * 一年期
     */
    ONE_YEAR("1年期"),
    /**
     * 五年期
     */
    FIVE_YEARS("5年期");

    private final String display;

    LprTermRange(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
