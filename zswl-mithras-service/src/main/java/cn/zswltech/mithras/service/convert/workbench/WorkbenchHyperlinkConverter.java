package cn.zswltech.mithras.service.convert.workbench;

import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkDto;
import cn.zswltech.mithras.service.mapper.model.workbench.WorkbenchHyperlink;
import org.mapstruct.Mapper;

/**
 * @author zhaozhengkang
 * @description 首页工作台-超链接
 * @date 2023-03-17
 */
@Mapper(componentModel = "spring")
public interface WorkbenchHyperlinkConverter {

    WorkbenchHyperlink dto2Entity(WorkbenchHyperlinkDto modifyReq);

    WorkbenchHyperlinkDto entity2Dto(WorkbenchHyperlink workbenchHyperlink);
}
