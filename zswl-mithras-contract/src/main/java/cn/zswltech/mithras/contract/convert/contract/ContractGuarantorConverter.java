package cn.zswltech.mithras.contract.convert.contract;

import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorAddREQ;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorListRSP;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorModifyREQ;
import cn.zswltech.mithras.contract.convert.contract.ContractTypeConversionWorker;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = ContractTypeConversionWorker.class, componentModel = "spring")
public interface ContractGuarantorConverter {


    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "toJsonString")
    @Mapping(source = "guarantorIds", target = "guarantorIds", qualifiedByName = "toJsonString")
    ContractGuarantor reqToEntity(ContractGuarantorAddREQ req);

    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "toJsonString")
    @Mapping(source = "guarantorIds", target = "guarantorIds", qualifiedByName = "toJsonString")
    ContractGuarantor modifyToEntity(ContractGuarantorModifyREQ req);

    @Mapping(source = "guarantorIds", target = "guarantorIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "resolutionFileId", target = "resolutionFileId", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "relatContracts", target = "relatContracts", qualifiedByName = "jsonStringToStringList")
    ContractGuarantorListRSP entityToRSP(ContractGuarantor entity);


}
