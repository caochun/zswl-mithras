package cn.zswltech.mithras.payment.job.service;

public interface PaymentPublicInfoCopyRetryService {

    void copyIntervalTable(Long paymentId);
}
