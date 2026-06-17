package cn.zswltech.mithras.associationreport.application;

import java.util.List;

public interface AssociationReportCollectionFactPort {

    List<AssociationReportCollectionSnapshot> listByContractIds(List<Long> contractIds);

    List<AssociationReportCollectionSnapshot> listRentByReceiptId(Long receiptId);
}
