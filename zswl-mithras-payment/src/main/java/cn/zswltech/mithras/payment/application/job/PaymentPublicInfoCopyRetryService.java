package cn.zswltech.mithras.payment.application.job;

public interface PaymentPublicInfoCopyRetryService {

    void copyIntervalTable(Long paymentId);
}
