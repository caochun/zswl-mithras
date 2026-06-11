package cn.zswltech.mithras.contract.enums.overdue;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 18:45
 */
public enum PrintingType implements PullDown {
    /**
     * 公章
     * 法人章
     * 公章+法人章
     */
    STAMP("公章"),
    LEGAL_STAMP("法人章"),
    STAMP_AND_LEGAL_STAMP("公章+法人章")
    ;


    private final String display;

    PrintingType(String display) {
        this.display = display;
    }
    @Override
    public String display() {
        return display;
    }
}
