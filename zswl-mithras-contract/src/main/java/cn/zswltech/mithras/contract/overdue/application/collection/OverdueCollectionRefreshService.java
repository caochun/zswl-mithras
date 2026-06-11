package cn.zswltech.mithras.contract.overdue.application.collection;

import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.overdue.application.job.SchedulingJobService;
import cn.zswltech.mithras.contract.overdue.dao.OverdueCollectionRecordDao;
import cn.zswltech.mithras.contract.overdue.mapper.model.OverdueCollection;
import cn.zswltech.mithras.foundation.port.ClientRiskExposureResolver;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OverdueCollectionRefreshService implements SchedulingJobService {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private CollectionOverdueHistoryResolver collectionOverdueHistoryResolver;
    @Resource
    private ClientRiskExposureResolver clientRiskExposureResolver;
    @Resource
    private OverdueCollectionRecordDao overdueCollectionRecordDao;

    @Override
    public void overdueClientInfoUpdateTask() {
        refreshAllClients();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void refreshAllClients() {
        log.info("overdueClientInfoUpdateTask start");
        List<OverdueCollection> allClient = contractBaseInfoMapper.getOverdueCollection(null);
        Map<Long, OverdueCollection> overdueClient = collectionOverdueHistoryResolver.overdueCollections(null)
                .stream().collect(Collectors.toMap(OverdueCollection::getClientId, v -> v));
        Map<Long, Long> stockRiskExposure = clientRiskExposureResolver.clientStockRiskExposureMap(
                allClient.stream().map(OverdueCollection::getClientId).collect(Collectors.toList()));

        Map<Long, OverdueCollection> allClientMap = allClient.stream()
                .collect(Collectors.toMap(OverdueCollection::getClientId, v -> v));

        allClientMap.forEach((clientId, item) -> {
            item.setRiskExposure(stockRiskExposure.get(clientId));
            if (overdueClient.containsKey(clientId)) {
                OverdueCollection overdueInfo = overdueClient.get(clientId);
                item.setLateCharge(overdueInfo.getLateCharge());
                if (overdueInfo.getOverdueRent() > 0L) {
                    item.setCurMaxOverdueDays(overdueInfo.getCurMaxOverdueDays());
                    item.setOverdueRent(overdueInfo.getOverdueRent());
                    item.setOverdue(true);
                } else {
                    item.setOverdue(false);
                }
            } else {
                item.setOverdue(false);
            }
        });

        overdueCollectionRecordDao.list().forEach(item -> {
            if (allClientMap.containsKey(item.getClientId())) {
                allClientMap.get(item.getClientId()).setId(item.getId());
            }
        });
        overdueCollectionRecordDao.saveOrUpdateBatch(allClientMap.values());
        log.info("overdueClientInfoUpdateTask end");
    }

    @Transactional(rollbackFor = Throwable.class)
    public void refreshClient(Long clientId) {
        List<Long> clientIds = Collections.singletonList(clientId);
        List<OverdueCollection> contractInfo = contractBaseInfoMapper.getOverdueCollection(clientIds);
        if (contractInfo.isEmpty()) {
            return;
        }
        List<OverdueCollection> overdueInfos = collectionOverdueHistoryResolver.overdueCollections(clientIds);
        Map<Long, Long> stockRiskExposure = clientRiskExposureResolver.clientStockRiskExposureMap(clientIds);

        OverdueCollection overdueCollection = contractInfo.get(0);
        overdueCollection.setRiskExposure(stockRiskExposure.get(clientId));
        if (!overdueInfos.isEmpty()) {
            OverdueCollection overdueInfo = overdueInfos.get(0);
            overdueCollection.setCurMaxOverdueDays(overdueInfo.getCurMaxOverdueDays());
            overdueCollection.setOverdueRent(overdueInfo.getOverdueRent());
            overdueCollection.setLateCharge(overdueInfo.getLateCharge());
            overdueCollection.setOverdue(true);
        } else {
            overdueCollection.setOverdue(false);
        }

        OverdueCollection dbOne = overdueCollectionRecordDao.getOne(Wrappers.<OverdueCollection>lambdaQuery()
                .eq(OverdueCollection::getClientId, clientId)
                .last(" limit 1"));
        if (dbOne != null) {
            overdueCollection.setId(dbOne.getId());
        }
        overdueCollectionRecordDao.saveOrUpdate(overdueCollection);
    }
}
