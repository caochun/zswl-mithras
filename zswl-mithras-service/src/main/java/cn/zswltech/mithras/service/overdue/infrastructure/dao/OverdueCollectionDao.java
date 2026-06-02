package cn.zswltech.mithras.service.overdue.infrastructure.dao;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.mapper.collection.CollectionOverdueHistoryMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.contract.overdue.application.query.CollectionPageQuery;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.OverdueCollectionMapper;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollection;
import cn.zswltech.mithras.service.service.client.ClientService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:30
 */
@Service
public class OverdueCollectionDao extends ServiceImpl<OverdueCollectionMapper, OverdueCollection> {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private CollectionOverdueHistoryMapper collectionOverdueHistoryMapper;
    @Resource
    private ClientService clientService;

    public Page<CollectionListDto> advancedList(CollectionPageQuery query){
        return baseMapper.advancedList(new Page<>(query.getPage(), query.getPageSize()), query);
    }


    public Integer updateByVersion(OverdueCollection entity) {
        Long oldVersion = entity.getLockVersion();
        entity.setLockVersion(oldVersion + 1);
        return baseMapper.update(entity, Wrappers.<OverdueCollection>lambdaUpdate()
                .eq(OverdueCollection::getId, entity.getId())
                .eq(OverdueCollection::getLockVersion, oldVersion));
    }

    /**
     * 实时更新客户逾期状态
     *
     * @param clientId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateByClientId(Long clientId) {
        List<Long> clientIds = Collections.singletonList(clientId);
        List<OverdueCollection> contractInfo = contractBaseInfoMapper.getOverdueCollection(clientIds);
        if (ObjectUtil.isEmpty(contractInfo)) {
            return;
        }
        List<OverdueCollection> overdueInfos = collectionOverdueHistoryMapper.getOverdueCollection(clientIds);
        Map<Long, Long> stockRiskExposure = clientService.clientStockRiskExposureMap(clientIds);

        OverdueCollection overdueCollection = contractInfo.get(0);
        // 风险敞口
        overdueCollection.setRiskExposure(stockRiskExposure.get(clientId));
        // 合并逾期信息
        if (ObjectUtil.isNotEmpty(overdueInfos)) {
            OverdueCollection overdueInfo = overdueInfos.get(0);
            overdueCollection.setCurMaxOverdueDays(overdueInfo.getCurMaxOverdueDays());
            overdueCollection.setOverdueRent(overdueInfo.getOverdueRent());
            overdueCollection.setLateCharge(overdueInfo.getLateCharge());
            overdueCollection.setOverdue(true);
        } else {
            overdueCollection.setOverdue(false);
        }
        // 查询当前数据库的逾期催收记录，若客户存在，则将原主键id赋值给newMap元素
        OverdueCollection dbOne = getOne(Wrappers.<OverdueCollection>lambdaQuery().eq(OverdueCollection::getClientId, clientId).last(" limit 1"));
        if (dbOne != null) {
            overdueCollection.setId(dbOne.getId());
        }
        // 更新或新增
        saveOrUpdate(overdueCollection);
    }

}
