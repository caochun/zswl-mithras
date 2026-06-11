package cn.zswltech.mithras.contract.core;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorAddREQ;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorListRSP;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorModifyREQ;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorRemoveREQ;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author vico
 * @description 合同-担保措施
 * @date 2022-08-12
 */
public interface ContractGuarantorService extends IService<ContractGuarantor> {

    Boolean add(ContractGuarantorAddREQ req);

    void add(Long contractId, List<ClientInfo> clientInfoList);

    void modify(ContractGuarantorModifyREQ req);

    List<ContractGuarantorListRSP> list(ContractIdListREQ req);

    Boolean remove(ContractGuarantorRemoveREQ req);

    List<ContractRelationRSP> contractByclient(ContractRelationREQ req);

    public List<Long> getIdsByContractId(Long contractId);

    void generateGuarantorContractCode(Long contractId);

    List<ContractGuarantor> listByContractId(Long contractId);

    List<ContractGuarantor> listByContractIds(Collection<Long> contractIds);

    Optional<List<Long>> getGuaranteeIdByContractIds(List<Long> contractIds);

    boolean existSpecificClient(Long clientId, Long contractId);
}
