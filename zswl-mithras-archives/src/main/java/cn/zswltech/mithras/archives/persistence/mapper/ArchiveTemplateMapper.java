package cn.zswltech.mithras.archives.persistence.mapper;

import cn.zswltech.mithras.dto.archives.ArchiveTemplateListREQ;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesFlatTemplateProjection;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesMustFileCountProjection;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesMustFileTypeCountProjection;
import cn.zswltech.mithras.archives.persistence.model.ArchiveFileType;
import cn.zswltech.mithras.archives.persistence.model.ArchiveTemplate;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @create: 2023-02-27
 **/
public interface ArchiveTemplateMapper extends CustomBaseMapper<ArchiveTemplate> {

    Page<ArchiveTemplate> pageList(Page<ArchiveTemplate> page, @Param("req") ArchiveTemplateListREQ req);

    List<ArchivesFlatTemplateProjection> flatTemplate(@Param("templateIds") List<Long> templateIds, @Param("fileType") String fileType);

    List<ArchivesFlatTemplateProjection> archiveFileList(@Param("archiveId") Long archiveId, @Param("content") String content, @Param("groupName") String groupName);

    List<ArchivesMustFileCountProjection> mustFileCount(@Param("archiveIds") List<Long> archiveIds);

    List<ArchivesMustFileTypeCountProjection> mustFileTypeCount(@Param("templateIds") List<Long> templateIds);

    List<ArchiveFileType> noFileTypes(@Param("archiveId") Long archiveId, @Param("templateId") Long templateId);


}
