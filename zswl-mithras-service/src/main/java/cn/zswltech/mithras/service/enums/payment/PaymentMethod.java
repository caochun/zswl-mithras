package cn.zswltech.mithras.service.enums.payment;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 15:16
 */
@AllArgsConstructor
@Getter
public enum PaymentMethod implements PullDown {

    WIRE_TRANSFER("电汇"),
    PJ("票据"),
    WY("网银"),
    XYZ("信用证"),
    YQZL("银企直联"),
    WTZF("委托支付"),
    REFUND_MARGIN("保证金退款"),
    REFUND_MARGIN_DEDUCT("保证金抵扣"),
    REFUND_WARRANTY("质保金退款"),
//    REFUND_WARRANTY_DEDUCT("质保金抵扣"),
    ;

    public final String display;

    @Override
    public String display() {
        return display;
    }

    public static PaymentMethod findByName(String name) {
        for (PaymentMethod item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static PaymentMethod findByDisplay(String name) {
        for (PaymentMethod item : values()) {
            if (Objects.equals(item.display(), name)) {
                return item;
            }
        }
        return null;
    }
}
