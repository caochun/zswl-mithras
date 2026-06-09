package cn.zswltech.mithras.projectprocess.excel.payment;

import java.time.LocalDate;

public class PaymentActualDetailData {
    private Long paymentId;
    private LocalDate paidInDate;
    private Long paidInAmount;

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDate getPaidInDate() {
        return paidInDate;
    }

    public void setPaidInDate(LocalDate paidInDate) {
        this.paidInDate = paidInDate;
    }

    public Long getPaidInAmount() {
        return paidInAmount;
    }

    public void setPaidInAmount(Long paidInAmount) {
        this.paidInAmount = paidInAmount;
    }
}
