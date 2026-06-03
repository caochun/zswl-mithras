package cn.zswltech.mithras.archives.application.template;

import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.ArchiveFileTypeMapper;
import cn.zswltech.mithras.archives.infrastructure.persistence.model.ArchiveFileType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @create: 2023-02-27
 **/
@Slf4j
@Service
public class ArchiveFileTypeService extends ServiceImpl<ArchiveFileTypeMapper, ArchiveFileType> {
}
