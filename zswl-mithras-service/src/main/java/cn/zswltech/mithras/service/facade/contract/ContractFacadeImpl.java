package cn.zswltech.mithras.service.facade.contract;

import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.service.contract.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ContractFacadeImpl implements ContractFacade {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;

    @Override
    public ContractBaseInfo getContractById(Long id) {
        return contractBaseInfoService.getById(id);
    }

    @Override
    public List<ContractBaseInfo> listContractByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return contractBaseInfoService.listByIds(ids);
    }

    @Override
    public List<ContractBaseInfo> listContract(LambdaQueryWrapper<ContractBaseInfo> wrapper) {
        return contractBaseInfoService.list(wrapper);
    }

    @Override
    public void updateContract(ContractBaseInfo contract) {
        contractBaseInfoService.updateById(contract);
    }

    @Override
    public ContractReceipt getReceiptById(Long id) {
        return contractReceiptService.getById(id);
    }

    @Override
    public List<ContractReceipt> listReceiptByContractId(Long contractId) {
        return contractReceiptService.listByContractId(contractId);
    }

    @Override
    public List<ContractReceipt> listReceiptByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return contractReceiptService.listByIds(ids);
    }

    @Override
    public List<ContractRentActual> listRentActual(LambdaQueryWrapper<ContractRentActual> wrapper) {
        return contractRentActualService.list(wrapper);
    }

    @Override
    public ContractLeasePrice getLeasePriceByContractId(Long contractId) {
        return contractLeasePriceService.getByContractId(contractId);
    }

    @Override
    public List<ContractLeasePrice> getLeasePriceByContractIds(List<Long> contractIds) {
        return contractLeasePriceService.listByContractIds(contractIds);
    }
}
