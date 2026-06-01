package cn.zswltech.mithras.service.mapper.archives;

import cn.zswltech.mithras.dto.archives.ArchivesListREQ;
import cn.zswltech.mithras.service.mapper.dto.ArchivesManagementDTO;
import cn.zswltech.mithras.service.mapper.model.archives.ArchivesManagement;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @create: 2023-02-27
 **/
public interface ArchivesManagementMapper extends CustomBaseMapper<ArchivesManagement> {

    Page<ArchivesManagementDTO> pageList(Page<ArchivesManagementDTO> page, @Param("dto") ArchivesListREQ req);


}
