package cn.zswltech.mithras.archives.persistence.mapper;

import cn.zswltech.mithras.dto.archives.ArchiveTemplateListREQ;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesMustFileTypeCountProjection;
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

    List<ArchivesMustFileTypeCountProjection> mustFileTypeCount(@Param("templateIds") List<Long> templateIds);
}
