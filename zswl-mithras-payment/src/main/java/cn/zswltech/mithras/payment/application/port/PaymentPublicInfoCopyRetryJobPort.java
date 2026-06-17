package cn.zswltech.mithras.payment.application.port;

public interface PaymentPublicInfoCopyRetryJobPort {

    void copyIntervalTable(Long paymentId);
}
