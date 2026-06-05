package cn.zswltech.mithras.workbench.application.convert;
import org.mapstruct.Mapper;

/**
 *
 * @author: jackerhe
 * @date: 2023/3/17 1:48 下午
 **/
@Mapper(uses = WorkbenchTypeConversionWorker.class, componentModel = "spring")
public interface WorkbenchShortcutsConverter {

}
