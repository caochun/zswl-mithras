package cn.zswltech.mithras.application.orchestration.adapter.contract;

import cn.zswltech.mithras.contract.overdue.application.collection.ContractDepositBalanceResolver;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Component
public class ContractDepositBalanceResolverAdapter implements ContractDepositBalanceResolver {

    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public Map<Long, Long> depositBalances(Set<Long> contractIds) {
        return marginBaseInfoService.getDepositBalances(contractIds);
    }
}
