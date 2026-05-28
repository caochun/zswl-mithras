package cn.zswltech.mithras.service.convert.workbench;

import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import org.mapstruct.Mapper;

/**
 *
 * @author: jackerhe
 * @date: 2023/3/17 1:48 下午
 **/
@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface WorkbenchShortcutsConverter {

}
