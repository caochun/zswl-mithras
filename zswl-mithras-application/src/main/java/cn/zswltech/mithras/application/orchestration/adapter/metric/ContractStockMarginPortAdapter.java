package cn.zswltech.mithras.application.orchestration.adapter.metric;

import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.metric.financialcloudmetric.port.ContractStockMarginPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Component
public class ContractStockMarginPortAdapter implements ContractStockMarginPort {

    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public Map<Long, Long> getCollectionAmountByContractIds(Set<Long> contractIds) {
        return marginBaseInfoService.getCollectionAmountByContractIds(contractIds);
    }
}
