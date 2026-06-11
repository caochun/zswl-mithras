package cn.zswltech.mithras.contract.overdue.dao;

import cn.zswltech.mithras.contract.overdue.mapper.DocPrintingMapper;
import cn.zswltech.mithras.contract.overdue.mapper.model.DocPrinting;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author zhaozhengkang
 * @description 文书用印
 * @date 2024-11-04
 */
@Service
public class DocPrintingDao extends ServiceImpl<DocPrintingMapper, DocPrinting> {
    public Integer updateByVersion(DocPrinting entity) {
        Long oldVersion = entity.getLockVersion();
        entity.setLockVersion(oldVersion + 1);
        return baseMapper.update(entity, Wrappers.<DocPrinting>lambdaUpdate()
                .eq(DocPrinting::getId, entity.getId())
                .eq(DocPrinting::getLockVersion, oldVersion));
    }
}