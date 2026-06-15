package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.collection.application.CollectionOverdueHistoryService;
import cn.zswltech.mithras.collection.application.CollectionOverdueRecordInfoService;
import cn.zswltech.mithras.collection.application.job.CollectionPenaltyInterestJobSupportPort;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionOverdueHistory;
import cn.zswltech.mithras.collection.model.CollectionOverdueRecordInfo;
import cn.zswltech.mithras.contract.overdue.application.collection.OverdueCollectionRefreshService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class CollectionPenaltyInterestJobSupportPortAdapter implements CollectionPenaltyInterestJobSupportPort {

    @Resource
    private CollectionOverdueRecordInfoService collectionOverdueRecordInfoService;
    @Resource
    private CollectionOverdueHistoryService collectionOverdueHistoryService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private OverdueCollectionRefreshService overdueCollectionRefreshService;

    @Override
    public Integer getDailyRate(Long contractId) {
        return collectionOverdueRecordInfoService.getDailyRate(contractId);
    }

    @Override
    public Map<Long, Integer> getDailyRateBatch(List<Long> contractIds) {
        return collectionOverdueRecordInfoService.getDailyRateBatch(contractIds);
    }

    @Override
    public List<CollectionOverdueRecordInfo> getLastRecordByCollectionId(List<Long> collectionIds) {
        return collectionOverdueRecordInfoService.getLastRecordByCollectionId(collectionIds);
    }

    @Override
    public void overdueSnapshot() {
        collectionOverdueHistoryService.overdueSnapshot();
    }

    @Override
    public void overdueSnapshot(LocalDate localDate) {
        collectionOverdueHistoryService.overdueSnapshot(localDate);
    }

    @Override
    public List<CollectionOverdueHistory> getHistoryByDay(LocalDate localDate) {
        return collectionOverdueHistoryService.getHistoryByDay(localDate);
    }

    @Override
    public List<CollectionOverdueHistory> getHistoryByDay(Long collectionId) {
        return collectionOverdueHistoryService.getHistoryByDay(collectionId);
    }

    @Override
    public void doRerunPenaltyInterest(List<Long> collectionIds) {
        collectionOverdueRecordInfoService.doRerunPenaltyInterest(collectionIds);
    }

    @Override
    public void modifyWriteOffStatus(List<Long> collectionIds) {
        collectionOverdueRecordInfoService.modifyWriteOffStatus(collectionIds);
    }

    @Override
    public void updateCollectionBaseInfoBatchById(List<CollectionBaseInfo> collectionBaseInfos) {
        collectionBaseInfoService.updateBatchById(collectionBaseInfos);
    }

    @Override
    public void saveOverdueRecordBatch(List<CollectionOverdueRecordInfo> recordInfos) {
        collectionOverdueRecordInfoService.saveBatch(recordInfos);
    }

    @Override
    public void refreshClient(Long clientId) {
        overdueCollectionRefreshService.refreshClient(clientId);
    }
}
