package cn.zswltech.mithras.archives.application.port;

import cn.zswltech.mithras.archives.persistence.projection.ArchivesManagementProjection;
import cn.zswltech.mithras.dto.archives.ArchivesListREQ;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ArchivesManagementQueryPort {

    Page<ArchivesManagementProjection> pageList(Page<ArchivesManagementProjection> page, ArchivesListREQ req);
}
