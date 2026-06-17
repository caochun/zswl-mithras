package cn.zswltech.mithras.archives.application.port;

import cn.zswltech.mithras.archives.persistence.model.ArchiveFileType;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesFlatTemplateProjection;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesMustFileCountProjection;

import java.util.List;

public interface ArchivesTemplateFileQueryPort {

    List<ArchivesFlatTemplateProjection> flatTemplate(List<Long> templateIds, String fileType);

    List<ArchivesFlatTemplateProjection> archiveFileList(Long archiveId, String content, String groupName);

    List<ArchivesMustFileCountProjection> mustFileCount(List<Long> archiveIds);

    List<ArchiveFileType> noFileTypes(Long archiveId, Long templateId);
}
