package cn.zswltech.mithras.application.orchestration.adapter.archives;

import cn.zswltech.mithras.application.orchestration.archives.mapper.ArchivesTemplateFileQueryMapper;
import cn.zswltech.mithras.archives.application.port.ArchivesTemplateFileQueryPort;
import cn.zswltech.mithras.archives.persistence.model.ArchiveFileType;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesFlatTemplateProjection;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesMustFileCountProjection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ArchivesTemplateFileQueryPortAdapter implements ArchivesTemplateFileQueryPort {

    @Resource
    private ArchivesTemplateFileQueryMapper archivesTemplateFileQueryMapper;

    @Override
    public List<ArchivesFlatTemplateProjection> flatTemplate(List<Long> templateIds, String fileType) {
        return archivesTemplateFileQueryMapper.flatTemplate(templateIds, fileType);
    }

    @Override
    public List<ArchivesFlatTemplateProjection> archiveFileList(Long archiveId, String content, String groupName) {
        return archivesTemplateFileQueryMapper.archiveFileList(archiveId, content, groupName);
    }

    @Override
    public List<ArchivesMustFileCountProjection> mustFileCount(List<Long> archiveIds) {
        return archivesTemplateFileQueryMapper.mustFileCount(archiveIds);
    }

    @Override
    public List<ArchiveFileType> noFileTypes(Long archiveId, Long templateId) {
        return archivesTemplateFileQueryMapper.noFileTypes(archiveId, templateId);
    }
}
