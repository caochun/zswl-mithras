package cn.zswltech.mithras.application.orchestration.archives.mapper;

import cn.zswltech.mithras.archives.persistence.projection.ArchivesManagementProjection;
import cn.zswltech.mithras.dto.archives.ArchivesListREQ;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArchivesManagementQueryMapper {

    Page<ArchivesManagementProjection> pageList(Page<ArchivesManagementProjection> page, @Param("dto") ArchivesListREQ req);
}
