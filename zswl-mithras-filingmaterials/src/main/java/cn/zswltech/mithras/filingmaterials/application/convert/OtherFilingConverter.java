package cn.zswltech.mithras.filingmaterials.application.convert;

import cn.zswltech.mithras.dto.filingmaterials.OtherPageListREQ;
import cn.zswltech.mithras.dto.filingmaterials.OtherPageSelectDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = FilingMaterialsTypeConversionWorker.class)
public interface OtherFilingConverter {

    @Mapping(target = "startDateFrom", source = "startDateFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "startDateTo", source = "startDateTo", qualifiedByName = "endOfDay")
    @Mapping(target = "endDateFrom", source = "endDateFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "endDateTo", source = "endDateTo", qualifiedByName = "endOfDay")
    OtherPageSelectDTO listReqToListDto(OtherPageListREQ req);
}
