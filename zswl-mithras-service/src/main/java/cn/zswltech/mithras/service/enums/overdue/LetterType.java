package cn.zswltech.mithras.service.enums.overdue;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 17:03
 */
public enum LetterType implements PullDown {
    /**
     * 催收函及相关函件
     */
    Collection("催收函及相关函件"),
    /**
     * 律师函
     */
    Lawyer("律师函"),
    ;

    private final String display;

    LetterType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
