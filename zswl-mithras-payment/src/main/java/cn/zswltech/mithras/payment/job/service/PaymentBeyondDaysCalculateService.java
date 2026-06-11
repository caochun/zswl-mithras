package cn.zswltech.mithras.payment.job.service;

public interface PaymentBeyondDaysCalculateService {

    void calculateBeyondDays(String paymentCode);
}
