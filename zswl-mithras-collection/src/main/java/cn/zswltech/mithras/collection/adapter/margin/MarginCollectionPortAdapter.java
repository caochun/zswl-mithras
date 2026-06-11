package cn.zswltech.mithras.collection.adapter.margin;

import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.margin.application.port.MarginCollectionPort;
import cn.zswltech.mithras.margin.application.port.model.MarginCollectionInfo;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MarginCollectionPortAdapter implements MarginCollectionPort {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;

    @Override
    public List<MarginCollectionInfo> listEarnestMoneyWrittenOffByContractIds(Collection<Long> contractIds) {
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getContractId, contractIds)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.EARNEST_MONEY.name())
                        .in(CollectionBaseInfo::getWriteOffStatus,
                                CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(),
                                CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()))
                .stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> sumCollectionRecordAmountByCollectionIdsBefore(Collection<Long> collectionIds, LocalDate actualDate) {
        return collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                        .in(CollectionRecordInfo::getCollectionId, collectionIds)
                        .le(CollectionRecordInfo::getCollectionDate, actualDate))
                .stream()
                .collect(Collectors.toMap(CollectionRecordInfo::getCollectionId,
                        e -> LongUtil.null2zero(e.getCollectionAmount()),
                        (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
    }

    private MarginCollectionInfo toInfo(CollectionBaseInfo collectionBaseInfo) {
        MarginCollectionInfo info = new MarginCollectionInfo();
        info.setId(collectionBaseInfo.getId());
        info.setContractId(collectionBaseInfo.getContractId());
        info.setPaymentId(collectionBaseInfo.getPaymentId());
        return info;
    }
}
