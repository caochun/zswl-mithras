package cn.zswltech.mithras.document.mapper;


import cn.zswltech.mithras.document.materialsfile.dto.NewestMaterialsDto;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @create: 2022-07-21
 **/
public interface MaterialsListLibMapper extends BaseMapper<MaterialsListLib> {


    List<MaterialsListLib> newestMaterials(@Param("dto") NewestMaterialsDto dto);
}
