package cn.zswltech.mithras.application.orchestration.adapter.customer;

import cn.zswltech.mithras.customer.mobile.VisitRecordMaterialPort;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VisitRecordMaterialPortAdapter implements VisitRecordMaterialPort {
    private static final String VISIT_RECORD = "VISIT_RECORD";

    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public List<VisitRecordMaterial> listByVisitRecordIds(Collection<Long> visitRecordIds) {
        if (visitRecordIds == null || visitRecordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, VISIT_RECORD)
                        .in(MaterialsList::getBelongId, visitRecordIds))
                .stream()
                .map(this::toVisitRecordMaterial)
                .collect(Collectors.toList());
    }

    private VisitRecordMaterial toVisitRecordMaterial(MaterialsList source) {
        VisitRecordMaterial material = new VisitRecordMaterial();
        material.setBelongId(source.getBelongId());
        material.setOssFilename(source.getOssFilename());
        material.setFilename(source.getFilename());
        return material;
    }
}
