package cn.zswltech.mithras.service.convert.contract;

import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageAddREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageModifyREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface ContractMortgageConverter {


    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "toJsonString")
    @Mapping(source = "mortgageIds", target = "mortgageIds", qualifiedByName = "toJsonString")
    @Mapping(source = "assessDate", target = "assessDate", qualifiedByName = "toLocalDateForYYYYMMDD")
    ContractMortgage reqToEntity(ContractMortgageAddREQ req);

    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "toJsonString")
    @Mapping(source = "mortgageIds", target = "mortgageIds", qualifiedByName = "toJsonString")
    @Mapping(source = "assessDate", target = "assessDate", qualifiedByName = "toLocalDateForYYYYMMDD")
    ContractMortgage modifyToEntity(ContractMortgageModifyREQ req);

    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "jsonStringToStringList")
    @Mapping(source = "mortgageIds", target = "mortgageIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "assessDate", target = "assessDate", qualifiedByName = "toStringForYYYYMMDD")
    ContractMortgageListRSP entityToRSP(ContractMortgage entity);


}
