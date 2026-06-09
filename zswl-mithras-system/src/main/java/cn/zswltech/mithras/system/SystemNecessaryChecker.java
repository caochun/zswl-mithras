package cn.zswltech.mithras.system;

import cn.zswltech.mithras.system.event.SystemSwitchRefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luyi
 */
@Slf4j
@Component
public class SystemNecessaryChecker implements ApplicationRunner {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void run(ApplicationArguments args) {
        // 初始化一些本地缓存配置
        try {
            SystemSwitchRefreshEvent systemSwitchRefreshEvent = new SystemSwitchRefreshEvent("application start");
            applicationEventPublisher.publishEvent(systemSwitchRefreshEvent);
        } catch (Exception e) {
            log.error("初始化本地缓存配置异常", e);
        }
    }
}
