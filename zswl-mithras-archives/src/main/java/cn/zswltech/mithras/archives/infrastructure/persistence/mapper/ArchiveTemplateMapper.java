package cn.zswltech.mithras.archives.infrastructure.persistence.mapper;

import cn.zswltech.mithras.dto.archives.ArchiveTemplateListREQ;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto.ArchivesFlatTempalteDTO;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto.ArchivesMastFileCountDTO;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto.ArchivesMastFileTypeCountDTO;
import cn.zswltech.mithras.archives.infrastructure.persistence.model.ArchiveFileType;
import cn.zswltech.mithras.archives.infrastructure.persistence.model.ArchiveTemplate;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @create: 2023-02-27
 **/
public interface ArchiveTemplateMapper extends CustomBaseMapper<ArchiveTemplate> {

    Page<ArchiveTemplate> pageList(Page<ArchiveTemplate> page, @Param("req") ArchiveTemplateListREQ req);

    List<ArchivesFlatTempalteDTO> flatTemplate(@Param("templateIds") List<Long> templateIds, @Param("fileType") String fileType);

    List<ArchivesFlatTempalteDTO> archiveFileList(@Param("archiveId") Long archiveId, @Param("content") String content, @Param("groupName") String groupName);

    List<ArchivesMastFileCountDTO> mustFileCount(@Param("archiveIds") List<Long> archiveIds);

    List<ArchivesMastFileTypeCountDTO> mustFileTypeCount(@Param("templateIds") List<Long> templateIds);

    List<ArchiveFileType> noFileTypes(@Param("archiveId") Long archiveId, @Param("templateId") Long templateId);


}
