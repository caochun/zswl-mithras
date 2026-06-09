package cn.zswltech.mithras.service.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseContractRentActualPort;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.core.application.ContractRentActualService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Component
public class AfterLeaseContractRentActualPortAdapter implements AfterLeaseContractRentActualPort {
    @Resource
    private ContractRentActualService contractRentActualService;

    @Override
    public List<ContractRentActual> listByContractIds(Collection<Long> contractIds) {
        return contractRentActualService.listByContractIds(contractIds);
    }
}
