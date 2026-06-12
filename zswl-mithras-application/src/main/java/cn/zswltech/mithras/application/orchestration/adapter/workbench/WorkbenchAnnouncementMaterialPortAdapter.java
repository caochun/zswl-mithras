package cn.zswltech.mithras.application.orchestration.adapter.workbench;

import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.workbench.application.WorkbenchAnnouncementMaterial;
import cn.zswltech.mithras.workbench.application.WorkbenchAnnouncementMaterialPort;
import cn.zswltech.mithras.workbench.enums.AnnouncementMetricMaterialEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WorkbenchAnnouncementMaterialPortAdapter implements WorkbenchAnnouncementMaterialPort {
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public List<WorkbenchAnnouncementMaterial> listImages(Set<Long> belongIds) {
        return materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                        .in(MaterialsList::getBelongId, belongIds)
                        .eq(MaterialsList::getBusinessType, AnnouncementMetricMaterialEnum.IMAGE.businessModule())
                        .eq(MaterialsList::getMaterialsType, AnnouncementMetricMaterialEnum.IMAGE.name()))
                .stream()
                .map(this::toMaterial)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkbenchAnnouncementMaterial> listImages(Long belongId) {
        return materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBelongId, belongId)
                        .eq(MaterialsList::getBusinessType, AnnouncementMetricMaterialEnum.IMAGE.businessModule())
                        .eq(MaterialsList::getMaterialsType, AnnouncementMetricMaterialEnum.IMAGE.name()))
                .stream()
                .map(material -> {
                    WorkbenchAnnouncementMaterial result = toMaterial(material);
                    result.setPreviewUrl(materialsListService.getPreviewUrl(material.getOssFilename(), 86400));
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void removeImages(Long belongId) {
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, belongId)
                .eq(MaterialsList::getBusinessType, AnnouncementMetricMaterialEnum.IMAGE.businessModule())
                .eq(MaterialsList::getMaterialsType, AnnouncementMetricMaterialEnum.IMAGE.name()));
    }

    @Override
    public String getPreviewUrl(String ossFilename, int expiry) {
        return materialsListService.getPreviewUrl(ossFilename, expiry);
    }

    private WorkbenchAnnouncementMaterial toMaterial(MaterialsList material) {
        WorkbenchAnnouncementMaterial result = new WorkbenchAnnouncementMaterial();
        result.setId(material.getId());
        result.setBelongId(material.getBelongId());
        result.setOssFilename(material.getOssFilename());
        return result;
    }
}
