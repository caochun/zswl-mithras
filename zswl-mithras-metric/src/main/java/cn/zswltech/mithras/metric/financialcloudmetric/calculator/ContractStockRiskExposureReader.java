package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.model.MarginBaseInfo;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class ContractStockRiskExposureReader {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;

    public Map<Long, Long> stockRiskExposureByContracts(Set<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return MapUtil.empty();
        }
        List<ContractBaseInfo> baseInfos = contractBaseInfoMapper.selectBatchIds(contractIds);
        if (CollUtil.isEmpty(baseInfos)) {
            return new HashMap<>();
        }

        List<PaymentActualDetail> actualDetails = paymentActualDetailMapper.selectList(
                Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .in(PaymentActualDetail::getContractId, contractIds));
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getCashFlowItem,
                                CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RENT.name())
                        .in(CollectionBaseInfo::getContractId, contractIds));

        Map<Long, List<CollectionBaseInfo>> collectionsByContract = CollUtil.isEmpty(collectionBaseInfos)
                ? new HashMap<>()
                : collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        Map<Long, List<PaymentActualDetail>> actualDetailsByContract = CollUtil.isEmpty(actualDetails)
                ? new HashMap<>()
                : actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        List<MarginBaseInfo> marginBaseInfos = marginBaseInfoMapper.selectList(
                Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getContractId, contractIds));
        Map<Long, MarginBaseInfo> marginByContract = marginBaseInfos.stream()
                .collect(Collectors.toMap(MarginBaseInfo::getContractId, o -> o, (v1, v2) -> v1));

        Map<Long, Long> stockRiskExposureMap = new HashMap<>();
        for (Long contractId : contractIds) {
            AtomicReference<Long> sum = new AtomicReference<>(0L);
            List<PaymentActualDetail> contractActualDetails = actualDetailsByContract.get(contractId);
            if (CollUtil.isNotEmpty(contractActualDetails)) {
                for (PaymentActualDetail paymentActualDetail : contractActualDetails) {
                    sum.updateAndGet(v -> v + LongUtil.null2zero(paymentActualDetail.getPaidInAmount()));
                }
            }
            List<CollectionBaseInfo> collections = collectionsByContract.get(contractId);
            if (CollUtil.isNotEmpty(collections)) {
                for (CollectionBaseInfo collection : collections) {
                    if (CashFlowItemEnum.FIRST_RENT.name().equals(collection.getCashFlowItem())) {
                        sum.updateAndGet(v -> v - LongUtil.null2zero(collection.getCollectionAmount()));
                    } else {
                        sum.updateAndGet(v -> v - LongUtil.null2zero(collection.getCollectionPrincipal()));
                    }
                }
            }
            MarginBaseInfo marginBaseInfo = marginByContract.get(contractId);
            if (marginBaseInfo != null) {
                sum.updateAndGet(v -> v - LongUtil.null2zero(marginBaseInfo.getCollectionAmount()));
            }
            stockRiskExposureMap.put(contractId, sum.get() < 0 ? 0L : sum.get());
        }
        return stockRiskExposureMap;
    }
}
