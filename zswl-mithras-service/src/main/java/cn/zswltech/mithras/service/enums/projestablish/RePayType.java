package cn.zswltech.mithras.service.enums.projestablish;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @author zhaozhengkang
 * @description 还款方式枚举
 * @since
 */
public enum RePayType implements PullDown {
    INDIRECT("间接还款"),
    DIRECT("直接还款");

    public String display;
    RePayType(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
