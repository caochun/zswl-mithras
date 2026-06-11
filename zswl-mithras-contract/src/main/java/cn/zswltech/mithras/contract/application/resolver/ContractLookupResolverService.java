package cn.zswltech.mithras.contract.application.resolver;

import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.dto.contract.ContractInfo;
import cn.zswltech.mithras.foundation.port.ContractInfoResolver;
import cn.zswltech.mithras.foundation.port.ContractNameResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;

@Service
public class ContractLookupResolverService implements ContractNameResolver, ContractInfoResolver {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;

    @Override
    public Map<Long, String> contractId2Name(Collection<Long> ids) {
        if (isNotEmpty(ids)) {
            return contractBaseInfoMapper.selectIds(new ArrayList<>(ids)).stream()
                    .collect(Collectors.toMap(ContractInfo::getId, ContractInfo::getContractCode));
        }
        return new HashMap<>();
    }

    @Override
    public Map<Long, String> receiptId2Name(Collection<Long> ids) {
        if (isNotEmpty(ids)) {
            return contractReceiptMapper.selectBatchIds(new ArrayList<>(ids)).stream()
                    .collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getReceiptCode));
        }
        return new HashMap<>();
    }

    @Override
    public Map<Long, ContractInfo> contractId2Info(Collection<Long> ids) {
        if (isNotEmpty(ids)) {
            return contractBaseInfoMapper.selectIds(new ArrayList<>(ids)).stream()
                    .collect(Collectors.toMap(ContractInfo::getId, contractInfo -> contractInfo));
        }
        return new HashMap<>();
    }
}
