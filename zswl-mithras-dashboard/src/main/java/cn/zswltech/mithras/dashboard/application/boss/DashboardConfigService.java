package cn.zswltech.mithras.dashboard.application.boss;

import cn.zswltech.mithras.dashboard.mapper.DashboardConfigMapper;
import cn.zswltech.mithras.dashboard.model.DashboardConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/16/11:20
 * @description
 */
@Slf4j
@Service
public class DashboardConfigService extends ServiceImpl<DashboardConfigMapper, DashboardConfig> {
    public List<DashboardConfig> listByKeys(Collection<String> keys) {
        LambdaQueryWrapper<DashboardConfig> query = Wrappers.lambdaQuery();
        query.in(DashboardConfig::getDashboardKey, keys);
        return this.list(query);
    }
}
