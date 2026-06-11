package cn.zswltech.mithras.contract.core;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryListRSP;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryModifyREQ;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryRemoveREQ;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
* @description 合同-承租人方案表
* @author vico
* @date 2022-08-12
*/
public interface ContractTenantryService extends IService<ContractTenantry> {

    void add(Long contractId, List<ClientInfo> clientInfoList);

    void modify(ContractTenantryModifyREQ req);

    void updateStockRiskExposure(Long contractId);

    List<ContractTenantryListRSP> list(ContractIdListREQ req);

    void remove(ContractTenantryRemoveREQ req);

    List<Long> getIdsByContractId(Long contractId);

    ContractTenantry getMain(Long contractId);

    List<ContractTenantry> listByContractId(Long contractId);

    List<ContractTenantry> listByClientIds(List<Long> clientIds);

    List<ContractTenantry> listByContractIds(Collection<Long> contractIds);

    //获取租金往来方
    Map<Long, String> listRentConcatAccountByContractId(List<Long> contractIds);

    boolean existSpecificClient(Long clientId, Long contractId);
}
