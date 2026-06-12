package cn.zswltech.mithras.fund.application.receiptrepay.port;

public interface FundReceiptRepayProcessEditablePort {

    boolean canEditRelatedProcess(Long fundReceiptRepayId);

    boolean canEditBatchProcess(Long batchId);
}
