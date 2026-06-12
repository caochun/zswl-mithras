package cn.zswltech.mithras.margin.application.port;

import cn.zswltech.mithras.margin.application.port.model.MarginCollectionRecordInfo;
import cn.zswltech.mithras.margin.application.port.model.MarginRefundPaymentInfo;

import java.util.Collection;
import java.util.List;

public interface MarginRecordSupportPort {

    void checkMarginView(Long marginId);

    void checkMarginRecordView(Long marginRecordId);

    void checkCollectionView(Long collectionId);

    int countUnfinishedCollectionsByContractId(Long contractId);

    List<MarginCollectionRecordInfo> listEarnestMoneyCollectionsByContractId(Long contractId);

    List<MarginCollectionRecordInfo> listCollectionsByIds(Collection<Long> collectionIds);

    MarginCollectionRecordInfo getCollectionById(Long collectionId);

    MarginCollectionRecordInfo getLastCollectionRecord(Long collectionId);

    List<MarginCollectionRecordInfo> listReceivedRentCollections(String collectionCode);

    String displayCollectionWriteOffStatus(String writeOffStatus);

    void sendRentReceivedMessage(MarginCollectionRecordInfo collection, Long paidInAmount, java.time.LocalDate paidInDate);

    boolean canSettleContractAfterMarginCompleted(Long contractId);

    void contractSettle(Long contractId);

    void pushMarginRefundPayments(Long contractId, List<MarginRefundPaymentInfo> payments);

    void withdrawBankFlow(Long recordId, String recordMainTable, Long amount);
}
