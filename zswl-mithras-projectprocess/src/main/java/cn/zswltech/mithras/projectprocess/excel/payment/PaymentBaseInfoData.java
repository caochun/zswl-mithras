package cn.zswltech.mithras.projectprocess.excel.payment;

public class PaymentBaseInfoData {
    private Long id;
    private Long applyPaymentAmount;
    private Long earnestMoney;
    private Long downPayment;
    private Long consultingFee;
    private Long nominalPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplyPaymentAmount() {
        return applyPaymentAmount;
    }

    public void setApplyPaymentAmount(Long applyPaymentAmount) {
        this.applyPaymentAmount = applyPaymentAmount;
    }

    public Long getEarnestMoney() {
        return earnestMoney;
    }

    public void setEarnestMoney(Long earnestMoney) {
        this.earnestMoney = earnestMoney;
    }

    public Long getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Long downPayment) {
        this.downPayment = downPayment;
    }

    public Long getConsultingFee() {
        return consultingFee;
    }

    public void setConsultingFee(Long consultingFee) {
        this.consultingFee = consultingFee;
    }

    public Long getNominalPrice() {
        return nominalPrice;
    }

    public void setNominalPrice(Long nominalPrice) {
        this.nominalPrice = nominalPrice;
    }
}
