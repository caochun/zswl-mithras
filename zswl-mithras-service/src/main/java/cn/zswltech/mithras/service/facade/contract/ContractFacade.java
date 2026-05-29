package cn.zswltech.mithras.service.facade.contract;

import cn.zswltech.mithras.service.mapper.model.contract.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Contract domain facade.
 * All cross-domain access to contract data should go through this interface.
 */
public interface ContractFacade {

    // ========== ContractBaseInfo ==========

    ContractBaseInfo getContractById(Long id);
    List<ContractBaseInfo> listContractByIds(Collection<Long> ids);
    List<ContractBaseInfo> listContract(LambdaQueryWrapper<ContractBaseInfo> wrapper);
    void updateContract(ContractBaseInfo contract);

    // ========== ContractReceipt ==========

    ContractReceipt getReceiptById(Long id);
    List<ContractReceipt> listReceiptByContractId(Long contractId);
    List<ContractReceipt> listReceiptByIds(Collection<Long> ids);

    // ========== ContractRentActual ==========

    List<ContractRentActual> listRentActual(LambdaQueryWrapper<ContractRentActual> wrapper);

    // ========== ContractLeasePrice ==========

    ContractLeasePrice getLeasePriceByContractId(Long contractId);
    List<ContractLeasePrice> getLeasePriceByContractIds(List<Long> contractIds);
}
