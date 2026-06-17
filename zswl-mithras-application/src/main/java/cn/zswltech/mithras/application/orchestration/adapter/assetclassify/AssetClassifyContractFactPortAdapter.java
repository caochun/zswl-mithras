package cn.zswltech.mithras.application.orchestration.adapter.assetclassify;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyContractFactPort;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyContractSnapshot;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AssetClassifyContractFactPortAdapter implements AssetClassifyContractFactPort {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;

    @Override
    public List<AssetClassifyContractSnapshot> listContractsByIds(Collection<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractBaseInfoMapper.selectBatchIds(contractIds)
                .stream()
                .map(item -> new AssetClassifyContractSnapshot(item.getId(), item.getBizType(),
                        item.getContractCode(), item.getLeaseType()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> countRemainingPhasesByContractIds(Collection<Long> contractIds, LocalDate cashFlowDateAfter) {
        if (CollUtil.isEmpty(contractIds)) {
            return Collections.emptyMap();
        }
        return contractRentActualMapper.selectList(Wrappers.<ContractRentActual>lambdaQuery()
                        .in(ContractRentActual::getContractId, contractIds)
                        .gt(ContractRentActual::getCashFlowDate, cashFlowDateAfter))
                .stream()
                .collect(Collectors.toMap(ContractRentActual::getContractId, item -> 1, Integer::sum));
    }

    @Override
    public Set<Long> listExistingReceiptIds(Collection<Long> receiptIds) {
        if (CollUtil.isEmpty(receiptIds)) {
            return Collections.emptySet();
        }
        return contractReceiptMapper.selectBatchIds(receiptIds)
                .stream()
                .map(ContractReceipt::getId)
                .collect(Collectors.toSet());
    }
}
