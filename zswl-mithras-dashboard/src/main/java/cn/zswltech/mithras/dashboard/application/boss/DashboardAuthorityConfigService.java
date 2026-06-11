package cn.zswltech.mithras.dashboard.application.boss;

import cn.zswltech.mithras.dashboard.mapper.DashboardAuthorityConfigMapper;
import cn.zswltech.mithras.dashboard.mapper.model.DashboardAuthorityConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/16/11:25
 * @description
 */
@Slf4j
@Service
public class DashboardAuthorityConfigService extends ServiceImpl<DashboardAuthorityConfigMapper, DashboardAuthorityConfig> {
    public List<DashboardAuthorityConfig> listByUserId(Long userId) {
        LambdaQueryWrapper<DashboardAuthorityConfig> query = Wrappers.lambdaQuery();
        query.eq(DashboardAuthorityConfig::getUserId, userId);
        return this.list(query);
    }
}
