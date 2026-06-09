package cn.zswltech.mithras.service.config.qlexpress;

import cn.zswltech.mithras.service.plugin.qlexpress.ExcelAndOperator;
import cn.zswltech.mithras.service.plugin.qlexpress.ExcelIfOperator;
import cn.zswltech.mithras.service.plugin.qlexpress.ExcelOrOperator;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/2/14
 * @description
 */
@Configuration
public class QLExpressConfig {
    @Resource
    private QLExpressProperties qlExpressProperties;

    @Bean
    public ExpressRunner expressRunner() {
        // 开启沙箱模式(最高安全级别即可符合业务场景，沙箱模式只允许通过自定义函数/操作符/宏与应用交互, 不允许与 JVM 中的类产生交互)
        QLExpressRunStrategy.setSandBoxMode(true);
        // 实例化表达式执行器
        ExpressRunner expressRunner = new ExpressRunner(true, qlExpressProperties.isTrace());
        // 自定义excel if函数 (不能使用if，if作为语法关键字，内置于解析器中，使用if会被直接当作关键字解析)
        // 详见 com.ql.util.express.parse.KeyWordDefine4Java.keyWords
        expressRunner.addFunction("excel_if", new ExcelIfOperator());
        // 自定义excel and函数 (不能使用and，and被框架设置为&&的别名，遇到时会以&&操作符处理)
        // 详见com.ql.util.express.parse.NodeTypeManager.NodeTypeManager(com.ql.util.express.parse.KeyWordDefine4Java)
        expressRunner.addFunction("excel_and", new ExcelAndOperator());
        // 自定义excel or函数 (不能使用or，or被框架设置为||的别名，遇到时会以||操作符处理)
        // 详见com.ql.util.express.parse.NodeTypeManager.NodeTypeManager(com.ql.util.express.parse.KeyWordDefine4Java)
        expressRunner.addFunction("excel_or", new ExcelOrOperator());
        return expressRunner;
    }
}
