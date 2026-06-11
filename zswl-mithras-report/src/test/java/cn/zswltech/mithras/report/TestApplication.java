package cn.zswltech.mithras.report;

import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;


/**
 *
 */

@Slf4j
@ServletComponentScan("cn.zswltech.mithras.guanbao.config")
@MapperScan({"cn.zswltech.mithras.document.mapper", "cn.zswltech.lib.futurelog.save.mapper"})
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"cn.zswltech"})
@EnableSwagger2
@RestController
public class TestApplication {
    @Resource
    private ProjEstablishBaseInfoMapper baseInfoMapper;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestApplication.class, args);
        int length = context.getBeanDefinitionNames().length;
        log.warn("Spring boot启动初始化了 {} 个 Bean", length);
    }

    @GetMapping("tt")
    public Object tt(@RequestParam("keyword") String keyword) {
        return baseInfoMapper.selectOne(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getProjName, "XXX"));
    }
}


