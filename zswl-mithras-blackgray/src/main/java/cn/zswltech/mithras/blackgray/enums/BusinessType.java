package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/12/15 11:15
 */
public enum BusinessType implements PullDown {
    INFORMATION_RELATED("涉信类"),
    INSURANCE_CATEGORY("保险类"),
    OTHER( "其他");

    private final String desc;
    BusinessType(String desc){
        this.desc = desc;
    }

    @Override
    public String display() {
        return this.desc;
    }
}
