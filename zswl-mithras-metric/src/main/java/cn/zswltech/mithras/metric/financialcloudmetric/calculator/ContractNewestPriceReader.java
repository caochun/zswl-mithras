package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAocPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractFactoringPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrice;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public Map<Long, Pair<Long, Integer>> queryNewestRate(Set<Long> contractIds) {
        return queryNewestPrice(contractIds).stream()
                .filter(priceLib -> priceLib.getLprPercent() != null && priceLib.getLprAddPercent() != null)
                .collect(Collectors.toMap(ContractPrice::getContractId,
                        item -> Pair.of(item.getContractAmount(), item.getLprPercent() + item.getLprAddPercent())));
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
}
