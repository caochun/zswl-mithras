package cn.zswltech.mithras.fund.domain.enums.receiptrepay;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/21 17:25
 */
public enum DepositCashFlowType implements PullDown {
    /**
     * 保证金支付
     */
    DEPOSIT_PAYMENT("保证金支付"),
    /**
     * 保证金退还
     */
    DEPOSIT_RETURN("保证金退还");

    DepositCashFlowType(String display) {
        this.display = display;
    }

    private String display;

    @Override
    public String display() {
        return display;
    }
}
