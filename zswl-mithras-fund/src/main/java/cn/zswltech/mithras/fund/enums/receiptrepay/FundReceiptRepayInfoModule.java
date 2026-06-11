package cn.zswltech.mithras.fund.enums.receiptrepay;

public enum FundReceiptRepayInfoModule {

    BASE_INFO("基本信息"),
    BORROWING("借款流入"),
    /**
     * 质押明细没有表，仅用于做版本比对
     */
    PLEDGE("质押明细"),
    CASH_FLOW("本金与利息一览表"),
    EXPENSE("费用一览表"),
    CASH_DEPOSIT("保证金明细"),
    RECEIPT_ACCOUNT("对方收款账户"),
    REPAY_ACCOUNT("还款账户"),
    MATERIALS_LIST("资料清单"),
    ;

    FundReceiptRepayInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static FundReceiptRepayInfoModule of(String code) {
        for (FundReceiptRepayInfoModule value : FundReceiptRepayInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
