package cn.zswltech.mithras.workbench.application.port;

import cn.zswltech.mithras.workbench.application.WorkbenchAnnouncementMaterial;

import java.util.List;
import java.util.Set;

public interface WorkbenchAnnouncementMaterialPort {

    List<WorkbenchAnnouncementMaterial> listImages(Set<Long> belongIds);

    List<WorkbenchAnnouncementMaterial> listImages(Long belongId);

    void removeImages(Long belongId);

    String getPreviewUrl(String ossFilename, int expiry);
}
