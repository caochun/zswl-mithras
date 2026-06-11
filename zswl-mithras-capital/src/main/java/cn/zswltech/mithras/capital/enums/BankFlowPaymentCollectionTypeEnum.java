package cn.zswltech.mithras.capital.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/18/13:59
 * @description
 */
@Getter
@AllArgsConstructor
public enum BankFlowPaymentCollectionTypeEnum implements PullDown {
    PAYMENT("付款"),
    COLLECTION("收款")
    ;

    private String display;

    @Override
    public String display() {
        return display;
    }
}
