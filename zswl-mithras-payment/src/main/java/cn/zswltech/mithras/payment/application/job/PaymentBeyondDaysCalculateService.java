package cn.zswltech.mithras.payment.application.job;

public interface PaymentBeyondDaysCalculateService {

    void calculateBeyondDays(String paymentCode);
}
