package cn.zswltech.mithras.document.materialsfile.convert;

import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.document.mapper.model.MaterialsListLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/16 09:52
 */
@Mapper(componentModel = "spring")
public interface MaterialsListConverter {

    @Mapping(source = "originId", target = "id")
    MaterialsList lib2Entity(MaterialsListLib materialsListLib);

    List<MaterialsList> lib2Entity(List<MaterialsListLib> materialsListLibs);

}
