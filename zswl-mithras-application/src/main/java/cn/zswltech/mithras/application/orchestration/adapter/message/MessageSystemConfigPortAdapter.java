package cn.zswltech.mithras.application.orchestration.adapter.message;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.message.port.MessageSystemConfigPort;
import cn.zswltech.mithras.system.mapper.SystemConfigMapper;
import cn.zswltech.mithras.system.mapper.model.SystemConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageSystemConfigPortAdapter implements MessageSystemConfigPort {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Override
    public String getEnabledConfigValue(String configKey) {
        SystemConfig config = systemConfigMapper.selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getConfigValue)
                .eq(SystemConfig::getConfigKey, configKey)
                .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));
        return config == null ? null : config.getConfigValue();
    }
}
