package cn.zswltech.mithras.contract.overdue.domain.docprinting;

import cn.zswltech.mithras.contract.overdue.mapper.model.DocPrinting;
import cn.zswltech.mithras.contract.overdue.mapper.model.DocPrintingLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.Printing;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:36
 */
@Mapper(componentModel = "spring")
public interface PrintingConverter {

    @Mapping(target = "id.id", source = "id")
    @Mapping(target = "code.code", source = "code")
    Printing po2Entity(DocPrinting byId);

    @Mapping(target = "id", source = "id.id")
    @Mapping(target = "code", source = "code.code")
    DocPrinting entity2Po(Printing aggregate);

    @Mapping(target = "id.id", source = "originId")
    @Mapping(target = "code.code", source = "code")
    Printing libPo2Entity(DocPrintingLib oneLib);
}
