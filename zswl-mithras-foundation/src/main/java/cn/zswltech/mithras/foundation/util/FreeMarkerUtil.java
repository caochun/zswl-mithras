package cn.zswltech.mithras.foundation.util;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Map;

/**
 * 生成代码
 *
 * @author wangchuanhao
 * @date 2022/7/17 11:26 PM
 */
@Slf4j
public class FreeMarkerUtil {

    private static Configuration freemarkerConfig = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    static {
        try {
            freemarkerConfig.setClassForTemplateLoading(FreeMarkerUtil.class, "/template/html");
            freemarkerConfig.setNumberFormat("#");
            freemarkerConfig.setClassicCompatible(true);
            freemarkerConfig.setDefaultEncoding("UTF-8");
            freemarkerConfig.setLocale(Locale.CHINA);
            freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 生成代码
     * @param options
     */
    public static void generate(OutputStream outputStream, String templateFileName, Map<String, Object> options) throws IOException, TemplateException {
        freemarkerConfig.getTemplate(templateFileName).process(options, new OutputStreamWriter(outputStream));
    }

}
