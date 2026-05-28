package cn.zswltech.mithras.service.enums.capital;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
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
