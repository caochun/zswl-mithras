package cn.zswltech.mithras.afterlease.adapter;

import cn.zswltech.mithras.afterlease.application.AfterLeaseContractPort;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Component
public class AfterLeaseContractPortAdapter implements AfterLeaseContractPort {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public List<ContractBaseInfo> listAllStartRent() {
        return contractBaseInfoService.listAllStartRent();
    }

    @Override
    public List<ContractBaseInfo> listActiveByClientId(Long clientId) {
        return contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, clientId)
                .in(ContractBaseInfo::getContractStatus, Arrays.asList(
                        ContractStatus.TAKE_EFFECT.name(),
                        ContractStatus.START_RENT.name(),
                        ContractStatus.SETTLE.name())));
    }

    @Override
    public List<ContractBaseInfo> listInRentContract(Long clientId) {
        return contractBaseInfoService.listInRentContract(clientId);
    }

    @Override
    public ContractBaseInfo getById(Long contractId) {
        return contractBaseInfoService.getById(contractId);
    }

    @Override
    public void updateById(ContractBaseInfo contractBaseInfo) {
        contractBaseInfoService.updateById(contractBaseInfo);
    }

    @Override
    public Long getStockRiskExposure(Long clientId) {
        return contractBaseInfoService.getStockRiskExposure(clientId, null, null);
    }
}
