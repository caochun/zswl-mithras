package cn.zswltech.mithras.afterlease.application;

import java.util.Collection;
import java.util.List;

public interface RentCollectionDetailDataPort {
    RentCollectionDetailSnapshot getRentCollectionById(Long collectionId);

    List<RentCollectionRecordSnapshot> listCollectionRecords(Long collectionId);

    RentCollectionOverdueContext getOverdueContext(Long collectionId);

    List<RentCollectionDetailSnapshot> listRentCollectionsByContractIds(Collection<Long> contractIds);

    List<RentCollectionDetailSnapshot> listRentCollectionsByContractId(Long contractId);

    List<RentCollectionDetailSnapshot> listOverdueRentCollectionsByReceiptIds(Collection<Long> receiptIds);

    List<RentCollectionDetailSnapshot> listRentCollectionsByIds(Collection<Long> collectionIds);

    List<RentCollectionLeasePriceSnapshot> listLeasePricesByContractIds(Collection<Long> contractIds);
}
