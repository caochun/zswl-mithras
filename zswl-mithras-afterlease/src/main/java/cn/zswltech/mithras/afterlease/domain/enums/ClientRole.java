package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/17 16:08
 */
public enum ClientRole implements PullDown {
    MAIN_LESSEE("主承租人"),
    LESSEE("承租人"),
    GUARANTEE("担保人"),
    CREDITOR("债权人"),
    DEBTOR("债务人"),
    MORTGAGE("抵押人"),
    PLEDGE("质押人"),
    ;

    private final String display;

    public String display() {
        return this.display;
    }

    ClientRole(String display) {
        this.display = display;
    }
}
