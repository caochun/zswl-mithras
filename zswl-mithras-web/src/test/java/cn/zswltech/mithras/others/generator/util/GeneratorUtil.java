package cn.zswltech.mithras.others.generator.util;

import cn.zswltech.mithras.others.generator.model.ClassInfo;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * 生成代码
 *
 * @author wangchuanhao
 * @date 2022/7/17 11:26 PM
 */
public class GeneratorUtil {

    private static Configuration freemarkerConfig = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    static {
        try {
            //2020-06-21 zhengkai 修复path问题导致jar无法运行而本地项目可以运行的bug
            freemarkerConfig.setClassForTemplateLoading(GeneratorUtil.class, "/generator/template");
            //freemarkerConfig.setTemplateLoader(new ClassTemplateLoader(FreemarkerUtil.class, "/templates/code-generator"));
            //freemarkerConfig.setDirectoryForTemplateLoading(new File("templates/code-generator"));
            freemarkerConfig.setNumberFormat("#");
            freemarkerConfig.setClassicCompatible(true);
            freemarkerConfig.setDefaultEncoding("UTF-8");
            freemarkerConfig.setLocale(Locale.CHINA);
            freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (Exception e) {
            //log.error(e.getMessage(), e);
        }
    }


    /**
     * 生成代码
     * @param tableSql
     * @param options
     * @param fileName
     */
    public static void generateCode(String tableSql, Map<String, Object> options,
                                    String dir, String fileName, String templateFileName) throws IOException, TemplateException {
        ClassInfo classInfo = TableParseUtil.processTableIntoClassInfo(tableSql, options);
        String actualFileName = dir + "/" + fileName;
        File file = new File(actualFileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        freemarkerConfig.getTemplate(templateFileName).process(options, new FileWriter(file));
        //System.out.println(fileName + " 生成成功");
    }

}
