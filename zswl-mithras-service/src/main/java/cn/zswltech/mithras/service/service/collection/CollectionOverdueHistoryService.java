package cn.zswltech.mithras.service.service.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.mapper.collection.CollectionOverdueHistoryMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionOverdueHistory;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* 逾期历史表
* @author vico
* @date 2023-05-31
*/
@Service
public class CollectionOverdueHistoryService extends ServiceImpl<CollectionOverdueHistoryMapper, CollectionOverdueHistory> {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Resource
    private CollectionOverdueHistoryService collectionOverdueHistoryService;

    @Resource
    private CollectionOverdueHistoryMapper collectionOverdueHistoryMapper;

    private final Object lock = new Object();

    @Transactional(rollbackFor = Throwable.class)
    public void overdueSnapshot(){
        SpringContextHolder.getBean(CollectionOverdueHistoryService.class).overdueSnapshot(LocalDate.now());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void overdueSnapshot(LocalDate localDate){
        if(ObjectUtil.isEmpty(localDate)) {
            localDate = LocalDate.now();
        }
        //查询过期
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                .lt(CollectionBaseInfo::getPlanCollectionDate, localDate.atStartOfDay())
                .gt(CollectionBaseInfo::getPhase, 0)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()));
        Long batchNumber = getBatchNumber();
        List<CollectionOverdueHistory> adds;
        if(ObjectUtil.isNotEmpty(collectionBaseInfos)){
            LocalDate finalLocalDate = localDate;
            adds = collectionBaseInfos.stream().map(base ->this.buildOverdueHistory(base, batchNumber, finalLocalDate)).collect(Collectors.toList());
            synchronized (lock){
                if(getBatchNumber().equals(batchNumber)){
                    collectionOverdueHistoryService.saveBatch(adds);
                }
            }
        }
    }

    public List<CollectionOverdueHistory> getHistoryByDay(LocalDate localDate){
        //查询某天最后的批次号
        CollectionOverdueHistory history = baseMapper.selectOne(Wrappers.<CollectionOverdueHistory>lambdaQuery()
                .between(CollectionOverdueHistory::getCreateTime, localDate.atStartOfDay(), localDate.atTime(23, 59, 59))
                .orderByDesc(CollectionOverdueHistory::getBatchNumber)
                .last(StringUtil.mysqlLimitOne()));
        if(ObjectUtil.isEmpty(history)){
            return Collections.emptyList();
        }
        return baseMapper.selectList(Wrappers.<CollectionOverdueHistory>lambdaQuery()
                .eq(CollectionOverdueHistory::getBatchNumber, history.getBatchNumber()));
    }

    public List<CollectionOverdueHistory> getHistoryByDay(Long collectionId){
        //查询某天最后的批次号
        return collectionOverdueHistoryMapper.getHistoryByDay(collectionId);
    }

    public Long getBatchNumber(){
        CollectionOverdueHistory history = this.baseMapper.selectOne(Wrappers.<CollectionOverdueHistory>lambdaQuery()
                .orderByDesc(CollectionOverdueHistory::getBatchNumber)
                .last(StringUtil.mysqlLimitOne()));
        return LongUtil.null2zero(history.getBatchNumber()) + 1L;
    }

    private CollectionOverdueHistory buildOverdueHistory(CollectionBaseInfo collectionBaseInfo, Long batchNumber, LocalDate localDate){
        CollectionOverdueHistory history = new CollectionOverdueHistory();
        history.setCollectionId(collectionBaseInfo.getId());
        history.setContractId(collectionBaseInfo.getContractId());
        history.setContractCode(collectionBaseInfo.getContractCode());
        history.setReceiptId(collectionBaseInfo.getReceiptId());
        history.setCollectionCode(collectionBaseInfo.getCode());
        history.setPhase(collectionBaseInfo.getPhase());
        history.setBatchNumber(batchNumber);
        history.setClientId(collectionBaseInfo.getClientId());
        history.setOverdueAmount(LongUtil.null2zero(collectionBaseInfo.getCashFlowAmount()) - (LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal()) + LongUtil.null2zero(collectionBaseInfo.getCollectionInterest())));
        //计算昨天的逾期，故逾期天数减一
        history.setOverdueDays(LocalDate.now().toEpochDay() - collectionBaseInfo.getPlanCollectionDate().toEpochDay());
        history.setCreateTime(localDate.atStartOfDay());
        return history;
    }
}