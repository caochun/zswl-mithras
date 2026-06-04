package cn.zswltech.mithras;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataSpecialDate;
import cn.zswltech.mithras.system.event.SystemSwitchRefreshEvent;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static cn.hutool.extra.spring.SpringUtil.getActiveProfile;
import static cn.hutool.extra.spring.SpringUtil.getApplicationContext;

/**
 * @author luyi
 */
@Slf4j
@Component
public class SystemNecessaryChecker implements ApplicationRunner {
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 在12月检查，明年的调休数据是否已经初始化到数据库中
     *
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            LocalDate now = LocalDate.now();
            int month = now.getMonthValue();
            if (month == 12) {
                int nextYear = now.getYear() + 1;
                LambdaQueryWrapper<BaseDataSpecialDate> query = Wrappers.lambdaQuery();
                query.eq(BaseDataSpecialDate::getYear, nextYear);
                List<BaseDataSpecialDate> list = baseDataSpecialDateService.list(query);
                if (CollectionUtil.isEmpty(list)) {
                    log.error("禁止启动。{}年放假调休日数据为空，请及时补全相关数据。" +
                                    "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。" +
                                    "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。" +
                                    "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。" +
                                    "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。",
                            nextYear, nextYear, nextYear, nextYear, nextYear);
                    if (!getActiveProfile().contains("pro")) {
                        //生产环境不阻塞启动，但是打印多次error日志
                        ((ConfigurableApplicationContext) getApplicationContext()).close();
                        System.exit(0);
                    }
                }
            }
        } catch (Exception e) {
            log.error("系统检查启动失败", e);
        }
        // 初始化一些本地缓存配置
        try {
            SystemSwitchRefreshEvent systemSwitchRefreshEvent = new SystemSwitchRefreshEvent("application start");
            applicationEventPublisher.publishEvent(systemSwitchRefreshEvent);
        } catch (Exception e) {
            log.error("初始化本地缓存配置异常", e);
        }
    }
}
