package cn.zswltech.mithras.archives.persistence.mapper;

import cn.zswltech.mithras.dto.archives.ArchivesListREQ;
import cn.zswltech.mithras.archives.persistence.dto.ArchivesManagementDTO;
import cn.zswltech.mithras.archives.persistence.model.ArchivesManagement;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @create: 2023-02-27
 **/
public interface ArchivesManagementMapper extends CustomBaseMapper<ArchivesManagement> {

    Page<ArchivesManagementDTO> pageList(Page<ArchivesManagementDTO> page, @Param("dto") ArchivesListREQ req);


}
