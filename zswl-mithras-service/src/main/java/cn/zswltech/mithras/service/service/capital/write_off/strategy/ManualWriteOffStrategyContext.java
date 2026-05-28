package cn.zswltech.mithras.service.service.capital.write_off.strategy;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.enums.capital.write_off.WriteOffBusinessModelEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.repository.PlatformApiHandler.log;

/**
 * @author bigbear
 * @date 2024/9/20 09:59
 * @description 核销策略的注册
 */
@Component
public class ManualWriteOffStrategyContext implements InitializingBean {
    private static final Map<WriteOffBusinessModelEnum, ManualWriteOffStrategyInterface> MANUAL_WRITE_OFF_STRATEGY_MAP = new ConcurrentHashMap<>();

    private void init() {
        Map<String, ManualWriteOffStrategyInterface> beansOfType = SpringUtil.getBeansOfType(ManualWriteOffStrategyInterface.class);
        for (ManualWriteOffStrategyInterface anInterface : beansOfType.values()) {
            MANUAL_WRITE_OFF_STRATEGY_MAP.put(anInterface.getWriteOffBusinessModel(), anInterface);
        }
    }

    public static ManualWriteOffStrategyInterface getInstance(String manualWriteOffBusinessModel) {
        WriteOffBusinessModelEnum businessModelEnum = WriteOffBusinessModelEnum.find(manualWriteOffBusinessModel);
        Assert.notNull(businessModelEnum, () -> MithrasException.newException("手动核销类型错误"));
        return MANUAL_WRITE_OFF_STRATEGY_MAP.get(businessModelEnum);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("手动核销策略上下文初始化中...........");
        init();
        log.info("手动核销策略上下文初始化完成...........共{}种核销策略, 分别初始化了：{}", MANUAL_WRITE_OFF_STRATEGY_MAP.size(),
                MANUAL_WRITE_OFF_STRATEGY_MAP.keySet().stream().map(WriteOffBusinessModelEnum::getDisplay).collect(Collectors.toList()));
    }
}
