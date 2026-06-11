package cn.zswltech.mithras.document.versioning;


import cn.zswltech.mithras.document.mapper.MaterialsListLibMapper;
import cn.zswltech.mithras.document.materialsfile.dto.NewestMaterialsDto;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件版本service
 */
@Slf4j
@Service
public class MaterialsListLibService extends ServiceImpl<MaterialsListLibMapper, MaterialsListLib> {

    public List<MaterialsListLib> newestMaterials(NewestMaterialsDto dto) {
        return baseMapper.newestMaterials(dto);
    }
}
