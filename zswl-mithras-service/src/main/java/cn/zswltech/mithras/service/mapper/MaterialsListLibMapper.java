package cn.zswltech.mithras.service.mapper;


import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @create: 2022-07-21
 **/
public interface MaterialsListLibMapper extends BaseMapper<MaterialsListLib> {


    List<MaterialsListLib> newestMaterials(@Param("dto") NewestMaterialsDto dto);
}
