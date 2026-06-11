package cn.zswltech.mithras.document.materialsfile;

import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class MaterialsListQueryService {

    @Resource
    private MaterialsListMapper materialsListMapper;

    public MaterialsList getById(Long id) {
        return materialsListMapper.selectById(id);
    }

    public List<MaterialsList> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return materialsListMapper.selectBatchIds(ids);
    }

    public List<MaterialsList> list(String businessType, List<String> materialsTypes, List<Long> belongIds) {
        if (belongIds == null || belongIds.isEmpty()) {
            return Collections.emptyList();
        }
        return materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, businessType)
                .in(materialsTypes != null && !materialsTypes.isEmpty(), MaterialsList::getMaterialsType, materialsTypes)
                .in(MaterialsList::getBelongId, belongIds));
    }
}
