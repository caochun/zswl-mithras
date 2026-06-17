package cn.zswltech.mithras.creditreport.service;

public interface CreditReportPaymentPort {

    Long getContractIdByPaymentId(Long paymentId);

    CreditReportPaymentProjectSnapshot getProjectSnapshotByPaymentId(Long paymentId);
}
