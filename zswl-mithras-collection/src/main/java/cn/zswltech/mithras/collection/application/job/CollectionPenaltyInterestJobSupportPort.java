package cn.zswltech.mithras.collection.application.job;

import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionOverdueHistory;
import cn.zswltech.mithras.collection.mapper.model.CollectionOverdueRecordInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CollectionPenaltyInterestJobSupportPort {

    Integer getDailyRate(Long contractId);

    Map<Long, Integer> getDailyRateBatch(List<Long> contractIds);

    List<CollectionOverdueRecordInfo> getLastRecordByCollectionId(List<Long> collectionIds);

    void overdueSnapshot();

    void overdueSnapshot(LocalDate localDate);

    List<CollectionOverdueHistory> getHistoryByDay(LocalDate localDate);

    List<CollectionOverdueHistory> getHistoryByDay(Long collectionId);

    void doRerunPenaltyInterest(List<Long> collectionIds);

    void modifyWriteOffStatus(List<Long> collectionIds);

    void updateCollectionBaseInfoBatchById(List<CollectionBaseInfo> collectionBaseInfos);

    void saveOverdueRecordBatch(List<CollectionOverdueRecordInfo> recordInfos);

    void refreshClient(Long clientId);
}
