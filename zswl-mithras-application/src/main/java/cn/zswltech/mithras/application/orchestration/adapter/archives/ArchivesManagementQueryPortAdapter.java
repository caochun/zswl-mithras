package cn.zswltech.mithras.application.orchestration.adapter.archives;

import cn.zswltech.mithras.application.orchestration.archives.mapper.ArchivesManagementQueryMapper;
import cn.zswltech.mithras.archives.application.port.ArchivesManagementQueryPort;
import cn.zswltech.mithras.archives.persistence.projection.ArchivesManagementProjection;
import cn.zswltech.mithras.dto.archives.ArchivesListREQ;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ArchivesManagementQueryPortAdapter implements ArchivesManagementQueryPort {

    @Resource
    private ArchivesManagementQueryMapper archivesManagementQueryMapper;

    @Override
    public Page<ArchivesManagementProjection> pageList(Page<ArchivesManagementProjection> page, ArchivesListREQ req) {
        return archivesManagementQueryMapper.pageList(page, req);
    }
}
