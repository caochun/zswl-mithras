package cn.zswltech.mithras.associationreport.application;

import java.util.Collection;
import java.util.List;

public interface AssociationReportMainBusinessFactPort {

    List<AssociationReportMainBusinessContractSnapshot> listAllStartRentContracts();

    AssociationReportMainBusinessLesseeSnapshot getMainLessee(Long contractId);

    AssociationReportMainBusinessClientSnapshot getClient(Long clientId);

    AssociationReportMainBusinessCorpSnapshot getCorpCommerceInfo(Long clientId);

    AssociationReportMainBusinessGuaranteeSnapshot getGuarantee(Long contractId);

    List<AssociationReportMainBusinessPaymentSnapshot> listPaymentsByContractIds(List<Long> contractIds);

    List<AssociationReportMainBusinessPaymentActualSnapshot> listPaymentActualByPaymentIds(Collection<Long> paymentIds);

    AssociationReportMainBusinessReceiptSnapshot getReceipt(Long receiptId);
}
