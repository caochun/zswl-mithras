package cn.zswltech.mithras.payment.application;

public interface PaymentWorkflowPort {

    PaymentWorkflowProcessSnapshot getProcessByInstanceId(String processInstanceId);

    PaymentWorkflowProcessSnapshot getLatestPassedProjectReviewProcess(Long projReviewId);

    PaymentWorkflowProcessSnapshot getRunningPaymentCreateProcess(Long paymentId);

    boolean isPaymentCreateProcess(String modelKey);

    boolean isPaymentActualDetailInProcess(Long paymentActualDetailId);

    boolean isAutoRentOrReceiptInProcess(Long contractId);
}
