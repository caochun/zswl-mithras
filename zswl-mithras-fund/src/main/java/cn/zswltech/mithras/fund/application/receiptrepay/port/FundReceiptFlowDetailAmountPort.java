package cn.zswltech.mithras.fund.application.receiptrepay.port;

public interface FundReceiptFlowDetailAmountPort {

    long sum(Long receiptRepayId, String cashFlowCode);
}
