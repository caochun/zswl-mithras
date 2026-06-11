package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractAocPriceMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractFactoringPriceMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAocPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractFactoringPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContractNewestPriceReader {

    @Resource
    private ContractAocPriceLibMapper contractAocPriceLibMapper;
    @Resource
    private ContractFactoringPriceLibMapper contractFactoringPriceLibMapper;
    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;
    @Resource
    private ContractFactoringPriceMapper contractFactoringPriceMapper;
    @Resource
    private ContractAocPriceMapper contractAocPriceMapper;

    public Map<Long, Pair<Long, Integer>> queryNewestRate(Set<Long> contractIds) {
        return queryNewestPrice(contractIds).stream()
                .filter(priceLib -> priceLib.getLprPercent() != null && priceLib.getLprAddPercent() != null)
                .collect(Collectors.toMap(ContractPrice::getContractId,
                        item -> Pair.of(item.getContractAmount(), item.getLprPercent() + item.getLprAddPercent())));
    }

    public Map<Long, Integer> queryNewestReceiptIrr(Set<Long> receiptIds) {
        if (receiptIds == null || receiptIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Integer> result = new HashMap<>();
        List<ContractReceiptLib> receiptLibList = contractReceiptMapper.queryNewestReceiptIrr(receiptIds);
        if (receiptLibList != null && !receiptLibList.isEmpty()) {
            result.putAll(receiptLibList.stream()
                    .filter(receipt -> ObjectUtil.isNotEmpty(receipt.getActualIrr()))
                    .collect(Collectors.toMap(ContractReceiptLib::getOriginId, ContractReceiptLib::getActualIrr, (k1, k2) -> k1)));
        }
        if (Objects.equals(result.size(), receiptIds.size())) {
            return result;
        }

        List<Long> needSelectIds = receiptIds.stream().filter(receiptId -> !result.containsKey(receiptId)).collect(Collectors.toList());
        List<ContractReceipt> contractReceipts = contractReceiptMapper.selectBatchIds(needSelectIds);
        Map<Long, Long> receiptContractMap = contractReceipts.stream().collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getContractId));
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectBatchIds(contractReceipts.stream().map(ContractReceipt::getContractId).distinct().collect(Collectors.toList()));
        Map<Long, Integer> contractIrrMap = queryContractIrr(contractBaseInfos);
        for (Long receiptId : needSelectIds) {
            Long contractId = receiptContractMap.get(receiptId);
            if (ObjectUtil.isNotEmpty(contractId)) {
                Integer irrPercent = contractIrrMap.get(contractId);
                if (ObjectUtil.isNotEmpty(irrPercent)) {
                    result.put(receiptId, irrPercent);
                }
            }
        }
        return result;
    }

    private List<ContractPrice> queryNewestPrice(Set<Long> contractIds) {
        List<ContractPrice> prices = new ArrayList<>();
        if (contractIds == null || contractIds.isEmpty()) {
            return prices;
        }
        prices.addAll(contractAocPriceLibMapper.queryNewestLib(contractIds));
        prices.addAll(contractFactoringPriceLibMapper.queryNewestLib(contractIds));
        prices.addAll(contractLeasePriceLibMapper.queryNewestLib(contractIds));
        return prices;
    }

    private Map<Long, Integer> queryContractIrr(List<ContractBaseInfo> contractBaseInfos) {
        Map<Long, Integer> result = new HashMap<>();
        if (contractBaseInfos == null || contractBaseInfos.isEmpty()) {
            return result;
        }
        Map<String, List<ContractBaseInfo>> collect = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getBizType));
        for (Map.Entry<String, List<ContractBaseInfo>> entry : collect.entrySet()) {
            List<Long> contractIds = entry.getValue().stream().map(ContractBaseInfo::getId).distinct().collect(Collectors.toList());
            if (ProjectBizType.ZL.name().equals(entry.getKey()) || ProjectBizType.ZZ.name().equals(entry.getKey())) {
                result.putAll(contractLeasePriceMapper.selectList(Wrappers.<ContractLeasePrice>lambdaQuery()
                                .in(ContractLeasePrice::getContractId, contractIds))
                        .stream().collect(Collectors.toMap(ContractLeasePrice::getContractId, ContractLeasePrice::getIrrPercent, (v1, v2) -> v1)));
            } else if (ProjectBizType.BL.name().equals(entry.getKey())) {
                result.putAll(contractFactoringPriceMapper.selectList(Wrappers.<ContractFactoringPrice>lambdaQuery()
                                .in(ContractFactoringPrice::getContractId, contractIds))
                        .stream().collect(Collectors.toMap(ContractFactoringPrice::getContractId, ContractFactoringPrice::getIrrPercent, (v1, v2) -> v1)));
            } else if (ProjectBizType.ZR.name().equals(entry.getKey())) {
                result.putAll(contractAocPriceMapper.selectList(Wrappers.<ContractAocPrice>lambdaQuery()
                                .in(ContractAocPrice::getContractId, contractIds))
                        .stream().collect(Collectors.toMap(ContractAocPrice::getContractId, ContractAocPrice::getIrrPercent, (v1, v2) -> v1)));
            }
        }
        return result;
    }
}
