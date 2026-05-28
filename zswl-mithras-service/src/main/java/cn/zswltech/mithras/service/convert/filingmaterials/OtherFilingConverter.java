package cn.zswltech.mithras.service.convert.filingmaterials;

import cn.zswltech.mithras.dto.filingmaterials.OtherPageListREQ;
import cn.zswltech.mithras.dto.filingmaterials.OtherPageSelectDTO;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TypeConversionWorker.class)
public interface OtherFilingConverter {

    @Mapping(target = "startDateFrom", source = "startDateFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "startDateTo", source = "startDateTo", qualifiedByName = "endOfDay")
    @Mapping(target = "endDateFrom", source = "endDateFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "endDateTo", source = "endDateTo", qualifiedByName = "endOfDay")
    OtherPageSelectDTO listReqToListDto(OtherPageListREQ req);
}
