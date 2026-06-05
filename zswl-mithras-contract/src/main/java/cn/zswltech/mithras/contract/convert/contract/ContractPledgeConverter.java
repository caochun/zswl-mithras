package cn.zswltech.mithras.contract.convert.contract;

import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeAddREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeModifyREQ;
import cn.zswltech.mithras.contract.convert.contract.ContractTypeConversionWorker;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = ContractTypeConversionWorker.class, componentModel = "spring")
public interface ContractPledgeConverter {

    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "toJsonString")
    @Mapping(source = "pledgeIds", target = "pledgeIds", qualifiedByName = "toJsonString")
    ContractPledge reqToEntity(ContractPledgeAddREQ req);

    @Mapping(source = "pledgeIds", target = "pledgeIds", qualifiedByName = "toJsonString")
    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "toJsonString")
    ContractPledge modifyToEntity(ContractPledgeModifyREQ req);

    @Mapping(source = "pledgeIds", target = "pledgeIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "jsonStringToStringList")
    ContractPledgeListRSP entityToRSP(ContractPledge entity);


}
