package cn.zswltech.mithras.service.convert.contract;

import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListRSP;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoModifyREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.excel.model.ContractExcelModel;
import cn.zswltech.mithras.service.mapper.dto.ContractListSelectDTO;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface ContractBaseInfoConverter {

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "leaseItemTypes", target = "leaseItemTypes", qualifiedByName = "jsonStringToStringList")
    ContractBaseInfoDetailRSP entityToDetailRSP(ContractBaseInfo baseInfo);

    ContractBaseInfo reviewToContract(ProjPricingBaseInfo pricingBaseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    ContractBaseInfoListRSP entityToListRsp(ContractBaseInfo baseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "toJsonString")
    @Mapping(source = "leaseItemTypes", target = "leaseItemTypes", qualifiedByName = "toJsonString")
    ContractBaseInfo modifyREQtoEntity(ContractBaseInfoModifyREQ modifyREQ);

    @Mapping(source = "createFrom", target = "createFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "createTo", target = "createTo", qualifiedByName = "endOfDay")
    @Mapping(source = "updateFrom", target = "updateFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "updateTo", target = "updateTo", qualifiedByName = "endOfDay")
    ContractListSelectDTO listREQtoSelectDTO(ContractBaseInfoListREQ listREQ);

    ContractExcelModel libEntity2exportRsp(ContractBaseInfoLib lastEffectLib);

//    @Mapping(target = "contractAmount",source = "applyCreditAmount")
//    @Mapping(target = "contractNo",source = "contractCode")
//    ContractBaseInfoRSP entityToRsp(ContractBaseInfo record);
//
//    List<ContractBaseInfoRSP> entitiesToRsps(List<ContractBaseInfo> records);
}
