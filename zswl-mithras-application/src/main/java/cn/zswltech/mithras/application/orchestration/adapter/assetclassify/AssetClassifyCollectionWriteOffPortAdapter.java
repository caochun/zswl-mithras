package cn.zswltech.mithras.application.orchestration.adapter.assetclassify;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyCollectionWriteOffPort;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssetClassifyCollectionWriteOffPortAdapter implements AssetClassifyCollectionWriteOffPort {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    @Override
    public Map<Long, Long> getRentPrincipalByReceiptIds(Collection<Long> receiptIds) {
        return getWrittenOffAmountByReceiptIds(receiptIds, CashFlowItemEnum.RENT.name(), true);
    }

    @Override
    public Map<Long, Long> getFirstRentAmountByReceiptIds(Collection<Long> receiptIds) {
        return getWrittenOffAmountByReceiptIds(receiptIds, CashFlowItemEnum.FIRST_RENT.name(), false);
    }

    private Map<Long, Long> getWrittenOffAmountByReceiptIds(Collection<Long> receiptIds, String cashFlowItem, boolean principalOnly) {
        if (receiptIds == null || receiptIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getReceiptId, receiptIds)
                        .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem)
                        .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                                CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())))
                .stream()
                .filter(base -> ObjectUtil.isNotEmpty(base.getReceiptId()))
                .collect(Collectors.toMap(CollectionBaseInfo::getReceiptId,
                        base -> principalOnly ? LongUtil.null2zero(base.getCollectionPrincipal()) : LongUtil.null2zero(base.getCollectionAmount()),
                        (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
    }
}
