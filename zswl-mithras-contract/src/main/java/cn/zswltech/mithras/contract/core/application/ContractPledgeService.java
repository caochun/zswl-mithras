package cn.zswltech.mithras.contract.core.application;


import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeAddREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeModifyREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeRemoveREQ;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author vico
 * @description 合同-质押措施
 * @date 2022-08-12
 */
public interface ContractPledgeService extends IService<ContractPledge> {

    Boolean add(ContractPledgeAddREQ req);

    void add(Long contractId, List<ClientInfo> clientInfoList);

    void modify(ContractPledgeModifyREQ req);

    List<ContractPledgeListRSP> list(ContractIdListREQ req);

    Boolean remove(ContractPledgeRemoveREQ req);

    void generatePledgeContractCode(Long contractId);

    List<ContractPledge> listByContractId(Long contractId);

    Optional<List<Long>> getPledgeIdByContractIds(List<Long> contractIds);

    List<ContractPledge> listByContractIds(Collection<Long> contractIds);

    boolean existSpecificClient(Long clientId, Long contractId);

    List<ContractRelationRSP> contractByclient(ContractRelationREQ req);
}
