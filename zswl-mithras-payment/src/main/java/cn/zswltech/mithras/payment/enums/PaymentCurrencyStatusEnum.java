package cn.zswltech.mithras.payment.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.Getter;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/23 17:12
 */
@Getter
public enum PaymentCurrencyStatusEnum implements PullDown {
    CNY("人民币"),
    USD("美元"),
    EUR("欧元"),
    GBP("英镑"),
    JPY("日元"),
    KHY("港币"),
    OTH("其他币种"),
    ;
    public String display;
    PaymentCurrencyStatusEnum(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
