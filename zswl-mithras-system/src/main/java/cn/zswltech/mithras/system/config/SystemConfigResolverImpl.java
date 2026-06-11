package cn.zswltech.mithras.system.config;

import cn.zswltech.mithras.foundation.port.SystemConfigResolver;
import cn.zswltech.mithras.system.mapper.SystemConfigMapper;
import cn.zswltech.mithras.system.mapper.model.SystemConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SystemConfigResolverImpl implements SystemConfigResolver {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Override
    public String getConfigValue(String configKey) {
        SystemConfig systemConfig = systemConfigMapper.selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .eq(SystemConfig::getConfigKey, configKey)
                .last("LIMIT 1"));
        return systemConfig == null ? null : systemConfig.getConfigValue();
    }
}
