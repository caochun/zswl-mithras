package cn.zswltech.mithras.assetclassify.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.assetclassify.mapper.AssetClassifyClientMapper;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientWithdrawalRatioListReq;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientWithdrawalRatioListRsp;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientWithdrawalRatioModifyReq;
import cn.zswltech.mithras.dto.assetclassify.WithdrawalRatioWrapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClientLib;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyContractFactPort;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyContractSnapshot;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyCollectionWriteOffPort;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyMarginAmountPort;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyPaymentBaseSnapshot;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyPaymentFactPort;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyClientLibService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 19:00
 */
@Service
public class AssetClassifyClientWithdrawalRatioService {

    @Resource
    private AssetClassifyClientMapper assetClassifyClientMapper;
    @Resource
    private AssetClassifyClientLibService assetClassifyClientLibService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private AssetClassifyCollectionWriteOffPort assetClassifyCollectionWriteOffPort;
    @Resource
    private AssetClassifyMarginAmountPort assetClassifyMarginAmountPort;
    @Resource
    private AssetClassifyPaymentFactPort assetClassifyPaymentFactPort;
    @Resource
    private AssetClassifyContractFactPort assetClassifyContractFactPort;

    public List<AssetClassifyClientWithdrawalRatioListRsp> listRatios(AssetClassifyClientWithdrawalRatioListReq req) {
        AssetClassifyClient classifyClient;
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            if (ObjectUtil.equals(req.getIsAuxiliary(), Boolean.TRUE)) {
                classifyClient = assetClassifyClientAuxiliaryLibService.getOne(Wrappers.<AssetClassifyClientAuxiliaryLib>lambdaQuery()
                        .eq(AssetClassifyClientAuxiliaryLib::getOriginId, req.getId())
                        .eq(AssetClassifyClientAuxiliaryLib::getVersion, req.getVersion()));
            } else {
                classifyClient = assetClassifyClientLibService.getOne(Wrappers.<AssetClassifyClientLib>lambdaQuery()
                        .eq(AssetClassifyClientLib::getOriginId, req.getId())
                        .eq(AssetClassifyClientLib::getVersion, req.getVersion()));
            }
        } else {
            classifyClient = assetClassifyClientMapper.selectById(req.getId());
        }
        if (ObjectUtil.isEmpty(classifyClient)) {
            throw new MithrasException("未查询到指定的分类客户或版本数据！");
        }
        String provisions = classifyClient.getProvisions();
        if (ObjectUtil.isEmpty(provisions)) {
            return Lists.emptyList();
        }
        List<WithdrawalRatioWrapper> wrappers = JSON.parseArray(provisions, WithdrawalRatioWrapper.class);
        Set<Long> contractIds = wrappers.stream()
                .map(WithdrawalRatioWrapper::getContractId).collect(Collectors.toSet());
        Map<Long, AssetClassifyContractSnapshot> contractMap = assetClassifyContractFactPort.listContractsByIds(contractIds)
                .stream()
                .collect(Collectors.toMap(AssetClassifyContractSnapshot::getId, v -> v));
        Map<Long, Integer> remainingPhaseMap = assetClassifyContractFactPort.countRemainingPhasesByContractIds(contractIds, LocalDate.now());

        Set<Long> receiptIds = wrappers.stream()
                .map(WithdrawalRatioWrapper::getReceiptId).collect(Collectors.toSet());
        Set<Long> existingReceiptIds = assetClassifyContractFactPort.listExistingReceiptIds(receiptIds);
        Map<Long, Long> receiptStockExposureMap = getStockRiskExposureByReceiptIds(new ArrayList<>(receiptIds));

        List<AssetClassifyClientWithdrawalRatioListRsp> rspList = new ArrayList<>();
        for (WithdrawalRatioWrapper wrapper : wrappers) {
            AssetClassifyClientWithdrawalRatioListRsp rsp = BeanUtil.copyProperties(wrapper, AssetClassifyClientWithdrawalRatioListRsp.class);
            AssetClassifyContractSnapshot contract = contractMap.getOrDefault(wrapper.getContractId(), new AssetClassifyContractSnapshot());
            rsp.setBizType(contract.getBizType());
            rsp.setContractCode(contract.getContractCode());
            rsp.setRemainingPhase(remainingPhaseMap.getOrDefault(wrapper.getContractId(), 0));
            // 投放额
            Set<Long> paymentIds = assetClassifyPaymentFactPort.listPaymentsByReceiptIds(
                            existingReceiptIds.contains(wrapper.getReceiptId()) ? ListUtil.toList(wrapper.getReceiptId()) : Collections.emptyList())
                    .stream().map(AssetClassifyPaymentBaseSnapshot::getId).collect(Collectors.toSet());
            if (CollectionUtil.isEmpty(paymentIds)) {
                rsp.setDeliveryAmount(0L);
            } else {
                BigDecimal totalDelivery = assetClassifyPaymentFactPort.getWrittenOffPaidAmountByPaymentIds(paymentIds)
                        .values().stream().map(LongUtil::null2zero)
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                rsp.setDeliveryAmount(totalDelivery.longValue());
            }
            // 获取借据下的风险敞口
            rsp.setStockExposure(receiptStockExposureMap.get(wrapper.getReceiptId()));
            rspList.add(rsp);
        }
        return rspList;
    }

    public void modifyRatioBatch(AssetClassifyClientWithdrawalRatioModifyReq req) {
        AssetClassifyClient classifyClient = assetClassifyClientMapper.selectById(req.getId());
        if (ObjectUtil.isEmpty(classifyClient)) {
            throw new MithrasException("记录不存在！");
        }
        classifyClient.setProvisions(JSON.toJSONString(req.getWithdrawalRatios()));
        assetClassifyClientMapper.updateById(classifyClient);
    }

    /**
     * 查询借据下风险敞口。
     * 计算规则 = 借据下实际付款 - 借据下收款本金核销值 - 借据下首期租金核销值 - 合同下保证金余额。
     */
    private Map<Long, Long> getStockRiskExposureByReceiptIds(List<Long> receiptIds) {
        if (CollectionUtil.isEmpty(receiptIds)) {
            return Collections.emptyMap();
        }
        List<AssetClassifyPaymentBaseSnapshot> paymentBaseInfos = assetClassifyPaymentFactPort.listPaymentsByReceiptIds(receiptIds);
        Map<Long, List<AssetClassifyPaymentBaseSnapshot>> receiptPaymentMap = paymentBaseInfos.stream()
                .filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal()))
                .collect(Collectors.groupingBy(AssetClassifyPaymentBaseSnapshot::getReceiptIdFinal));
        Map<Long, Long> receiptId2ContractId = paymentBaseInfos.stream()
                .filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal()))
                .collect(Collectors.toMap(AssetClassifyPaymentBaseSnapshot::getReceiptIdFinal, AssetClassifyPaymentBaseSnapshot::getContractId, (a, b) -> a));
        Set<Long> paymentIds = paymentBaseInfos.stream().map(AssetClassifyPaymentBaseSnapshot::getId).collect(Collectors.toSet());
        Map<Long, Long> paymentIdAmountMap = assetClassifyPaymentFactPort.getWrittenOffOrPartWrittenOffPaidAmountByPaymentIds(paymentIds);
        Map<Long, Long> receiptCollectionId = assetClassifyCollectionWriteOffPort.getRentPrincipalByReceiptIds(receiptIds);
        Map<Long, Long> paymentIdFistRentMap = assetClassifyCollectionWriteOffPort.getFirstRentAmountByReceiptIds(receiptIds);
        Map<Long, Long> receiptIdMarginMap = assetClassifyMarginAmountPort.getAmountByReceiptIds(receiptId2ContractId.keySet(), LocalDate.now());
        Map<Long, Long> receiptExposureMap = new HashMap<>();
        for (Long receiptId : receiptPaymentMap.keySet()) {
            long sumAmount = 0L;
            List<AssetClassifyPaymentBaseSnapshot> paymentList = receiptPaymentMap.get(receiptId);
            if (ObjectUtil.isNotEmpty(paymentList)) {
                sumAmount += paymentList.stream().map(AssetClassifyPaymentBaseSnapshot::getId).map(paymentIdAmountMap::get).mapToLong(LongUtil::null2zero).sum();
            }
            sumAmount -= LongUtil.null2zero(receiptCollectionId.get(receiptId));
            sumAmount -= paymentIdFistRentMap.getOrDefault(receiptId, 0L);
            sumAmount -= receiptIdMarginMap.getOrDefault(receiptId, 0L);
            receiptExposureMap.put(receiptId, Math.max(sumAmount, 0));
        }
        return receiptExposureMap;
    }
}
