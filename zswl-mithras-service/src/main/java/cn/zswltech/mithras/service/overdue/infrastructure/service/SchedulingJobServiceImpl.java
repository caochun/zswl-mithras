package cn.zswltech.mithras.service.overdue.infrastructure.service;

import cn.zswltech.mithras.service.mapper.collection.CollectionOverdueHistoryMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.overdue.application.job.SchedulingJobService;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.OverdueCollectionDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollection;
import cn.zswltech.mithras.service.service.client.ClientService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/23 10:28
 */
@Component
@Slf4j
public class SchedulingJobServiceImpl implements SchedulingJobService {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Resource
    private CollectionOverdueHistoryMapper collectionOverdueHistoryMapper;

    @Resource
    private OverdueCollectionDao overdueCollectionDao;

    @Resource
    private ClientService clientService;

    @Override
    @XxlJob("overdueClientInfoUpdateTask")
    public void overdueClientInfoUpdateTask() {
        log.info("overdueClientInfoUpdateTask start");
        // 获取所有起租客户
        List<OverdueCollection> allClient = contractBaseInfoMapper.getOverdueCollection(null);
        // 获取所有的逾期客户,转换成Map
        Map<Long, OverdueCollection> overdueClient = collectionOverdueHistoryMapper.getOverdueCollection(null)
                .stream().collect(Collectors.toMap(OverdueCollection::getClientId, v -> v));
        // 获取所有客户的存量风险敞口
        Map<Long, Long> stockRiskExposure = clientService.clientStockRiskExposureMap(allClient.stream().map(OverdueCollection::getClientId).collect(Collectors.toList()));

        Map<Long, OverdueCollection> allClientMap = allClient.stream()
                .collect(Collectors.toMap(OverdueCollection::getClientId, v -> v));

        allClientMap.forEach((clientId,item) -> {
            // 风险敞口
            item.setRiskExposure(stockRiskExposure.get(clientId));
            // 合并逾期信息
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
            }else {
                item.setOverdue(false);
            }
        });

        // 查询当前逾期催收记录，若客户存在，则将原主键id赋值给newMap元素
        overdueCollectionDao.list().forEach(item -> {
            if (allClientMap.containsKey(item.getClientId())) {
                allClientMap.get(item.getClientId()).setId(item.getId());
            }
        });
        // 批量更新
        overdueCollectionDao.saveOrUpdateBatch(allClientMap.values());
        log.info("overdueClientInfoUpdateTask end");
    }
}
