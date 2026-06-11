package cn.zswltech.mithras.contract.overdue.application.assembler;

import cn.zswltech.mithras.contract.overdue.application.command.PrintingAddCommand;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.Printing;
import cn.zswltech.mithras.contract.overdue.mapper.model.DocPrinting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 17:25
 */
@Mapper(componentModel = "spring")
public interface DocPrintingAssembler {

    PrintingListDto po2ListDto(DocPrinting po);

    List<PrintingListDto> po2ListDto(List<DocPrinting> pos);

    Printing command2Entity(PrintingAddCommand command);

    @Mapping(source = "id.id", target = "id")
    PrintingDetailDto entity2DetailDto(Printing printing);

    @Mapping(source = "id", target = "id.id")
    Printing detailToEntity(PrintingDetailDto dto);
}
