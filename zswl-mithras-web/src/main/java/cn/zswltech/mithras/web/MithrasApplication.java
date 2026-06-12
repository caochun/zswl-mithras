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
@ServletComponentScan("cn.zswltech.mithras.guanbao.config")
@MapperScan({"cn.zswltech.mithras.document.persistence.mapper",
        "cn.zswltech.mithras.customer.mapper",
        "cn.zswltech.mithras.customer.project.persistence.mapper",
        "cn.zswltech.mithras.customer.authorityrecord.persistence.mapper",
        "cn.zswltech.mithras.customer.externalcustomer.persistence.mapper",
        "cn.zswltech.mithras.third.externaldata.common.persistence.mapper",
        "cn.zswltech.mithras.third.externaldata.environmentpenalty.persistence.mapper",
        "cn.zswltech.mithras.third.externaldata.tianyancha.persistence.mapper",
        "cn.zswltech.mithras.third.externaldata.zhongdeng.persistence.mapper",
        "cn.zswltech.mithras.customer.hymx.persistence.mapper",
        "cn.zswltech.mithras.customer.infohistory.persistence.mapper",
        "cn.zswltech.mithras.customer.mobile.persistence.mapper",
        "cn.zswltech.mithras.customer.sandrecord.persistence.mapper",
        "cn.zswltech.mithras.customer.userref.persistence.mapper",
        "cn.zswltech.mithras.customer.vwsync.persistence.mapper",
        "cn.zswltech.mithras.system.audit.mapper",
        "cn.zswltech.mithras.message.persistence.mapper",
        "cn.zswltech.mithras.contract.mapper",
        "cn.zswltech.mithras.contract.overdue.mapper",
        "cn.zswltech.mithras.rating.mapper",
        "cn.zswltech.mithras.third.**.mapper",
        "cn.zswltech.lib.futurelog.save.mapper",
        "cn.zswltech.mithras.blackgray.persistence.mapper",
        "cn.zswltech.mithras.margin.persistence.mapper",
        "cn.zswltech.mithras.metric.financialcloudmetric.mapper",
        "cn.zswltech.mithras.ftp.newftp.mapper",
        "cn.zswltech.mithras.fund.persistence.mapper.credit",
        "cn.zswltech.mithras.fund.persistence.mapper.financial",
        "cn.zswltech.mithras.fund.persistence.mapper.financing",
        "cn.zswltech.mithras.fund.persistence.mapper.organization",
        "cn.zswltech.mithras.fund.directfinancing.persistence.mapper",
        "cn.zswltech.mithras.kpi.mapper",
        "cn.zswltech.mithras.finance.view.mapper",
        "cn.zswltech.mithras.archives.persistence.mapper",
        "cn.zswltech.mithras.workflow.persistence.mapper",
        "cn.zswltech.mithras.policy.persistence.mapper"
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
