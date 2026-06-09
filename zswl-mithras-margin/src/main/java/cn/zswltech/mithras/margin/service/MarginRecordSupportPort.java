package cn.zswltech.mithras.margin.service;

import cn.zswltech.mithras.margin.service.model.MarginCollectionRecordInfo;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;

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

    void pushMarginRefundPayments(Long contractId, List<CQ2PaymentVO> payments);

    void withdrawBankFlow(Long recordId, String recordMainTable, Long amount);
}
