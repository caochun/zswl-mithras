package cn.zswltech.mithras.contract.core.application;


import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageAddREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageModifyREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageRemoveREQ;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author vico
 * @description 合同-抵押措施
 * @date 2022-08-12
 */
public interface ContractMortgageService extends IService<ContractMortgage> {

    void add(ContractMortgageAddREQ req);

    void add(Long contractId, List<ClientInfo> clientInfoList);

    void modify(ContractMortgageModifyREQ req);

    List<ContractRelationRSP> contractByclient(ContractRelationREQ req);

    List<ContractMortgageListRSP> list(ContractIdListREQ req);

    Boolean remove(ContractMortgageRemoveREQ req);

    List<Long> getIdsByContractId(Long contractId);

    void generateMortgageContractCode(Long contractId);

    List<ContractMortgage> listByContractId(Long contractId);

    Optional<List<Long>> getMortgageIdByContractIds(List<Long> contractIds);

    List<ContractMortgage> listByContractIds(Collection<Long> contractIds);

    boolean existSpecificClient(Long clientId, Long contractId);
}
