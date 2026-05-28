package cn.zswltech.mithras.service.overdue.infrastructure.dao;

import cn.zswltech.mithras.service.overdue.application.query.LitigationPageQuery;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.mapper.LitigationRegistrationMapper;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.LitigationRegistration;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @description 诉讼登记
* @author zhaozhengkang
* @date 2024-10-30
*/
@Service
public class LitigationRegistrationDao extends ServiceImpl<LitigationRegistrationMapper, LitigationRegistration> {
    public Integer updateByVersion(LitigationRegistration entity) {
        Long oldVersion = entity.getLockVersion();
        entity.setLockVersion(oldVersion + 1);
        return baseMapper.update(entity, Wrappers.<LitigationRegistration>lambdaUpdate()
                .eq(LitigationRegistration::getId, entity.getId())
                .eq(LitigationRegistration::getLockVersion, oldVersion));
    }

    public Page<LitigationRegistration> advancedList(LitigationPageQuery query) {
        return baseMapper.advancedList(new Page<>(query.getPage(), query.getPageSize()), query);
    }
}