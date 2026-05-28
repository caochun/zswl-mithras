package cn.zswltech.mithras.web;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 */

@Slf4j
@ServletComponentScan("cn.zswltech.mithras.service.config.druid")
@MapperScan({"cn.zswltech.mithras.service.mapper",
        "cn.zswltech.mithras.factory.mapper",
        "cn.zswltech.mithras.service.providence.mapper",
        "cn.zswltech.lib.futurelog.save.mapper",
        "cn.zswltech.mithras.blackgray.mapper",
        "cn.zswltech.mithras.metric.financialcloudmetric.mapper",
        "cn.zswltech.mithras.service.service.newftp.mapper",
        "cn.zswltech.mithras.service.fund.direct.mapper",
        "cn.zswltech.mithras.kpi.mapper",
        "cn.zswltech.mithras.service.overdue.infrastructure.dao.mapper",
        "cn.zswltech.mithras.finance.view.mapper"
})
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"cn.zswltech"})
@EnableFeignClients(basePackages = {"cn.zswltech.mithras"})
@RestController
public class MithrasApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MithrasApplication.class, args);
        int length = context.getBeanDefinitionNames().length;
        log.info("Spring boot启动初始化了 {} 个 Bean", length);
    }

    /**
     * 探针
     */
    @SneakyThrows
    @RequestMapping("/api/ok")
    public String ok() {
//        SpringContextHolder.getBean(FileAuthenticationConfigService.class).setAuthenticationByPost(req);
        return "ok";
    }

}


