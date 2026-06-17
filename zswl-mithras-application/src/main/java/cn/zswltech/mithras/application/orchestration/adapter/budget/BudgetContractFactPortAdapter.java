package cn.zswltech.mithras.application.orchestration.adapter.budget;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.budget.application.port.BudgetContractFactPort;
import cn.zswltech.mithras.budget.application.port.BudgetContractFactSnapshot;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BudgetContractFactPortAdapter implements BudgetContractFactPort {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public List<BudgetContractFactSnapshot> listByIds(Collection<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractBaseInfoService.listByIds(contractIds).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private BudgetContractFactSnapshot convert(ContractBaseInfo contractBaseInfo) {
        BudgetContractFactSnapshot snapshot = new BudgetContractFactSnapshot();
        snapshot.setId(contractBaseInfo.getId());
        snapshot.setBizDeptId(contractBaseInfo.getBizDeptId());
        snapshot.setProjReviewId(contractBaseInfo.getProjReviewId());
        snapshot.setProjCode(contractBaseInfo.getProjCode());
        return snapshot;
    }
}
