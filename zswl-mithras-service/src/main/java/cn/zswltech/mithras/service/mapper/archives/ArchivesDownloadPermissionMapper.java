package cn.zswltech.mithras.service.mapper.archives;

import cn.zswltech.mithras.service.mapper.model.archives.ArchivesDownloadPermission;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @create: 2023-02-27
 **/
public interface ArchivesDownloadPermissionMapper extends CustomBaseMapper<ArchivesDownloadPermission> {

    int insertList(@Param("permissions") List<ArchivesDownloadPermission> permissions);

}
