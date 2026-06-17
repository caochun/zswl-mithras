package cn.zswltech.mithras.application.orchestration.archives.mapper;

import cn.zswltech.mithras.archives.persistence.model.ArchiveFileType;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesFlatTemplateProjection;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesMustFileCountProjection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArchivesTemplateFileQueryMapper {

    List<ArchivesFlatTemplateProjection> flatTemplate(@Param("templateIds") List<Long> templateIds, @Param("fileType") String fileType);

    List<ArchivesFlatTemplateProjection> archiveFileList(@Param("archiveId") Long archiveId, @Param("content") String content, @Param("groupName") String groupName);

    List<ArchivesMustFileCountProjection> mustFileCount(@Param("archiveIds") List<Long> archiveIds);

    List<ArchiveFileType> noFileTypes(@Param("archiveId") Long archiveId, @Param("templateId") Long templateId);
}
