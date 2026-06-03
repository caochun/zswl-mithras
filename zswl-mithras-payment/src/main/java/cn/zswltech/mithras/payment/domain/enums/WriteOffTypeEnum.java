package cn.zswltech.mithras.payment.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * 核销类型
 */
public enum WriteOffTypeEnum implements PullDown {
    MANUAL_RECORD("⼿⼯核销"),
    AUTO_RECORD("自动核销"),
    ;
    public String display;
    WriteOffTypeEnum(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
