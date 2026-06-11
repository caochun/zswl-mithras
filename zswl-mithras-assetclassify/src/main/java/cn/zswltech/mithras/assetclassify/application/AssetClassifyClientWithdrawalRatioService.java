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
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClientLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientLibService;
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
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;

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
        Map<Long, ContractBaseInfo> contractMap = contractBaseInfoMapper
                .selectBatchIds(contractIds).stream()
                .collect(Collectors.toMap(ContractBaseInfo::getId, v -> v));

        Set<Long> receiptIds = wrappers.stream()
                .map(WithdrawalRatioWrapper::getReceiptId).collect(Collectors.toSet());
        Map<Long, ContractReceipt> receipts = contractReceiptMapper.selectBatchIds(receiptIds).stream()
                .collect(Collectors.toMap(ContractReceipt::getId, v -> v));
        Map<Long, Long> receiptStockExposureMap = getStockRiskExposureByReceiptIds(new ArrayList<>(receiptIds));

        List<AssetClassifyClientWithdrawalRatioListRsp> rspList = new ArrayList<>();
        for (WithdrawalRatioWrapper wrapper : wrappers) {
            AssetClassifyClientWithdrawalRatioListRsp rsp = BeanUtil.copyProperties(wrapper, AssetClassifyClientWithdrawalRatioListRsp.class);
            ContractBaseInfo contract = contractMap.getOrDefault(wrapper.getContractId(), new ContractBaseInfo());
            rsp.setBizType(contract.getBizType());
            rsp.setContractCode(contract.getContractCode());
            int remainingParse = Math.toIntExact(contractRentActualMapper.selectCount(Wrappers.<ContractRentActual>lambdaQuery()
                    .eq(ContractRentActual::getContractId, wrapper.getContractId())
                    .gt(ContractRentActual::getCashFlowDate, LocalDate.now())));
            rsp.setRemainingPhase(remainingParse);
            ContractReceipt receipt = receipts.getOrDefault(wrapper.getReceiptId(), new ContractReceipt());
            // 投放额
            Set<Long> paymentIds = paymentBaseInfoMapper
                    .selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                            .eq(PaymentBaseInfo::getReceiptIdFinal, receipt.getId())).stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            if (CollectionUtil.isEmpty(paymentIds)) {
                rsp.setDeliveryAmount(0L);
            } else {
                BigDecimal totalDelivery = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getPaymentId, paymentIds)
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()))
                        .stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero)
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
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getReceiptIdFinal, receiptIds));
        Map<Long, List<PaymentBaseInfo>> receiptPaymentMap = paymentBaseInfos.stream()
                .filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal()))
                .collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptIdFinal));
        Map<Long, Long> receiptId2ContractId = paymentBaseInfos.stream()
                .filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal()))
                .collect(Collectors.toMap(PaymentBaseInfo::getReceiptIdFinal, PaymentBaseInfo::getContractId, (a, b) -> a));
        Set<Long> paymentIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        Map<Long, Long> paymentIdAmountMap = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(CollectionUtil.isNotEmpty(paymentIds), PaymentActualDetail::getPaymentId, paymentIds)
                        .in(PaymentActualDetail::getWriteOffStatus, ListUtil.toList(PaymentWriteOffStatus.WRITTEN_OFF.name(),
                                PaymentWriteOffStatus.PART_WRITTEN_OFF.name())))
                .stream()
                .filter(base -> LongUtil.null2zero(base.getPaidInAmount()) != 0)
                .collect(Collectors.toMap(PaymentActualDetail::getPaymentId, PaymentActualDetail::getPaidInAmount,
                        (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        Map<Long, Long> receiptCollectionId = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                                CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())))
                .stream()
                .filter(base -> ObjectUtil.isNotEmpty(base.getReceiptId()))
                .collect(Collectors.toMap(CollectionBaseInfo::getReceiptId,
                        base -> LongUtil.null2zero(base.getCollectionPrincipal()),
                        (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        Map<Long, Long> paymentIdFistRentMap = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.FIRST_RENT.name())
                        .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                                CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())))
                .stream()
                .filter(base -> ObjectUtil.isNotEmpty(base.getReceiptId()))
                .collect(Collectors.toMap(CollectionBaseInfo::getReceiptId,
                        base -> LongUtil.null2zero(base.getCollectionAmount()),
                        (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        Map<Long, Long> receiptIdMarginMap = marginBaseInfoService.getAmountByReceiptIds(receiptId2ContractId.keySet(), LocalDate.now());
        Map<Long, Long> receiptExposureMap = new HashMap<>();
        for (Long receiptId : receiptPaymentMap.keySet()) {
            long sumAmount = 0L;
            List<PaymentBaseInfo> paymentList = receiptPaymentMap.get(receiptId);
            if (ObjectUtil.isNotEmpty(paymentList)) {
                sumAmount += paymentList.stream().map(PaymentBaseInfo::getId).map(paymentIdAmountMap::get).mapToLong(LongUtil::null2zero).sum();
            }
            sumAmount -= LongUtil.null2zero(receiptCollectionId.get(receiptId));
            sumAmount -= paymentIdFistRentMap.getOrDefault(receiptId, 0L);
            sumAmount -= receiptIdMarginMap.getOrDefault(receiptId, 0L);
            receiptExposureMap.put(receiptId, Math.max(sumAmount, 0));
        }
        return receiptExposureMap;
    }
}
