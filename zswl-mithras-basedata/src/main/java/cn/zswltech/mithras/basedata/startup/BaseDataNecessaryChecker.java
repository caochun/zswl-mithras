package cn.zswltech.mithras.basedata.startup;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataSpecialDate;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static cn.hutool.extra.spring.SpringUtil.getActiveProfile;
import static cn.hutool.extra.spring.SpringUtil.getApplicationContext;

@Slf4j
@Component
public class BaseDataNecessaryChecker implements ApplicationRunner {

    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;

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
                    log.error("禁止启动。{}年放假调休日数据为空，请及时补全相关数据。"
                                    + "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。"
                                    + "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。"
                                    + "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。"
                                    + "\n禁止启动。{}年放假调休日数据为空，请及时补全相关数据。",
                            nextYear, nextYear, nextYear, nextYear, nextYear);
                    if (!getActiveProfile().contains("pro")) {
                        ((ConfigurableApplicationContext) getApplicationContext()).close();
                        System.exit(0);
                    }
                }
            }
        } catch (Exception e) {
            log.error("基础数据启动检查失败", e);
        }
    }
}
