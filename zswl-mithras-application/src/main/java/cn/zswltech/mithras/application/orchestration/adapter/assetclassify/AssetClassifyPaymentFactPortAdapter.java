package cn.zswltech.mithras.application.orchestration.adapter.assetclassify;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyPaymentBaseSnapshot;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyPaymentFactPort;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssetClassifyPaymentFactPortAdapter implements AssetClassifyPaymentFactPort {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;

    @Override
    public List<AssetClassifyPaymentBaseSnapshot> listPaymentsByReceiptIds(Collection<Long> receiptIds) {
        if (CollUtil.isEmpty(receiptIds)) {
            return Collections.emptyList();
        }
        return paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .in(PaymentBaseInfo::getReceiptIdFinal, receiptIds))
                .stream()
                .map(item -> new AssetClassifyPaymentBaseSnapshot(item.getId(), item.getContractId(), item.getReceiptIdFinal()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> getWrittenOffPaidAmountByPaymentIds(Collection<Long> paymentIds) {
        return getPaidAmountByPaymentIds(paymentIds, ListUtil.toList(WriteOffStatus.WRITTEN_OFF.name()));
    }

    @Override
    public Map<Long, Long> getWrittenOffOrPartWrittenOffPaidAmountByPaymentIds(Collection<Long> paymentIds) {
        return getPaidAmountByPaymentIds(paymentIds, ListUtil.toList(PaymentWriteOffStatus.WRITTEN_OFF.name(),
                PaymentWriteOffStatus.PART_WRITTEN_OFF.name()));
    }

    private Map<Long, Long> getPaidAmountByPaymentIds(Collection<Long> paymentIds, Collection<String> writeOffStatuses) {
        if (CollUtil.isEmpty(paymentIds)) {
            return Collections.emptyMap();
        }
        return paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getPaymentId, paymentIds)
                        .in(PaymentActualDetail::getWriteOffStatus, writeOffStatuses))
                .stream()
                .filter(base -> LongUtil.null2zero(base.getPaidInAmount()) != 0)
                .collect(Collectors.toMap(PaymentActualDetail::getPaymentId, PaymentActualDetail::getPaidInAmount,
                        (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
    }
}
