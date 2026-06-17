package cn.zswltech.mithras.payment.application.port;

public interface PaymentBeyondDaysCalculateJobPort {

    void calculateBeyondDays(String paymentCode);
}
