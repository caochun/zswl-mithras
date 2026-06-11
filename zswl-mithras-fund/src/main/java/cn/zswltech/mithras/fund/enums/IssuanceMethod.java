package cn.zswltech.mithras.fund.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/18 09:37
 */
public enum IssuanceMethod implements PullDown {
    //枚举值：公募，私募
    PUBLIC("公募"),
    PRIVATE("私募");;
    private final String display;

    IssuanceMethod(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
