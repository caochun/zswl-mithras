package cn.zswltech.mithras.contract.overdue.dao;

import cn.zswltech.mithras.contract.overdue.mapper.OverdueCollectionMapper;
import cn.zswltech.mithras.contract.overdue.mapper.model.OverdueCollection;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Basic overdue collection persistence used by the overdue aggregate.
 */
@Service
public class OverdueCollectionRecordDao extends ServiceImpl<OverdueCollectionMapper, OverdueCollection> {

    public Integer updateByVersion(OverdueCollection entity) {
        Long oldVersion = entity.getLockVersion();
        entity.setLockVersion(oldVersion + 1);
        return baseMapper.update(entity, Wrappers.<OverdueCollection>lambdaUpdate()
                .eq(OverdueCollection::getId, entity.getId())
                .eq(OverdueCollection::getLockVersion, oldVersion));
    }
}
