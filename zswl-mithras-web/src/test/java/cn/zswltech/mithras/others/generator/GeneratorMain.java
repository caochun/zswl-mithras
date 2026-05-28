package cn.zswltech.mithras.others.generator;

import cn.zswltech.mithras.others.generator.model.ClassInfo;
import cn.zswltech.mithras.others.generator.util.GeneratorUtil;
import cn.zswltech.mithras.others.generator.util.TableParseUtil;
import cn.zswltech.mithras.others.generator.util.TableUtil;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author wangchuanhao
 * @date 2022/7/17 11:45 PM
 */
public class GeneratorMain {

    public static final String MYSQL_URL = "jdbc:mysql://10.158.32.232:3306/mithras_pre?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=false&nullCatalogMeansCurrent=true";
    public static final String MYSQL_USER = "pre_user";
    public static final String MYSQL_PWD = "Zszl@2023De*#";

    // 自定义变量 决定了作者等 需要按需调整
    public static final String AUTHOR = System.getProperty("user.name");

    public static final String REQ_DIR = "/cn/zswltech/mithras/dto/creditreport";
    public static final String MAPPER_DIR = "/cn/zswltech/mithras/service/mapper/creditreport";
    public static final String SERVICE_DIR = "/cn/zswltech/mithras/service/service/creditreport";
    public static final String API_DIR = "/cn/zswltech/mithras/api/creditreport";
    public static final String CONTROLLER_DIR = "/cn/zswltech/mithras/service/controller/creditreport";
    public static final String CONVERTER_DIR = "/cn/zswltech/mithras/service/convert/kpi";
    public static final String ENTITY_DIR = "/cn/zswltech/mithras/service/mapper/model/creditreport";

    public static final String genDir = System.getProperty("user.dir");
    public static final String serviceGenDir = System.getProperty("user.dir") + "/zswl-mithras-service/src/main/java";
    public static final String apiGenDir = System.getProperty("user.dir") + "/zswl-mithras-api/src/main/java";

    public static void main(String[] args) throws TemplateException, IOException {
        List<String> tableNameList = Arrays.asList("ecl_execute_client_promotion_result");
        for (String tableName : tableNameList) {
            Map<String, Object> params = new HashMap<String, Object>() {{
                put("dataType", "sql");
                put("authorName", AUTHOR);
                //put("packageName", "com.webuy.whale.defend");
                //put("isWithPackage", false);
                put("isPackageType", true);
                put("isSwagger", true);
                put("isAutoImport", true);
                put("isComment", true);
                put("isLombok", true);
                // 忽略表名前缀
                put("ignorePrefix", "wd_");
                // TINYINT 需要转成什么类型 比如Integer、Byte、Boolean
                put("tinyintTransType", "Integer");
                put("nameCaseType", "CamelCase");
                // TIMESTAMP 需要转成什么类型 比如Date LocalDateTime（需要改模版）
                put("timeTransType", "LocalDateTime");

                // 模版中引用其他模块类所需的前缀路径
                put("reqDir", REQ_DIR.substring(1).replaceAll("\\/", "."));
                put("mapperDir", MAPPER_DIR.substring(1).replaceAll("\\/", "."));
                put("serviceDir", SERVICE_DIR.substring(1).replaceAll("\\/", "."));
                put("apiDir", API_DIR.substring(1).replaceAll("\\/", "."));
                put("controllerDir", CONTROLLER_DIR.substring(1).replaceAll("\\/", "."));
                put("entityDir", ENTITY_DIR.substring(1).replaceAll("\\/", "."));
            }};
            String tableSql = TableUtil.getDDLByTableName(tableName);
            ClassInfo classInfo = TableParseUtil.processTableIntoClassInfo(tableSql, params);
            params.put("tableName", tableName);
            params.put("classInfo", classInfo);

            genEntity(tableSql, params, classInfo);
            genAddREQ(tableSql, params, classInfo);
            genModifyREQ(tableSql, params, classInfo);
            genListREQ(tableSql, params, classInfo);
            genListRSP(tableSql, params, classInfo);
            genRemoveREQ(tableSql, params, classInfo);
            /*genMapper(tableSql, params, classInfo);
            genService(tableSql, params, classInfo);
            genApi(tableSql, params, classInfo);
            genController(tableSql, params, classInfo);*/
            //genConverter(tableSql, params, classInfo);
        }
    }

    private static void genConverter(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws
            TemplateException, IOException {
        params.put("packageName", CONVERTER_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, serviceGenDir + CONVERTER_DIR, classInfo.getClassName() + "Converter.java", "converter.ftl");
        System.out.println(classInfo.getClassName() + " converter 生成成功！！！");
    }


    public static void genRemoveREQ(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", REQ_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, apiGenDir + REQ_DIR, classInfo.getClassName() + "RemoveREQ.java", "removeREQ.ftl");
        System.out.println(classInfo.getClassName() + " removeREQ 生成成功！！！");
    }

    public static void genListRSP(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", REQ_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, apiGenDir + REQ_DIR, classInfo.getClassName() + "ListRSP.java", "listRSP.ftl");
        System.out.println(classInfo.getClassName() + " listRSP 生成成功！！！");
    }

    public static void genListREQ(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", REQ_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, apiGenDir + REQ_DIR, classInfo.getClassName() + "ListREQ.java", "listREQ.ftl");
        System.out.println(classInfo.getClassName() + " listREQ 生成成功！！！");
    }

    public static void genModifyREQ(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", REQ_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, apiGenDir + REQ_DIR, classInfo.getClassName() + "ModifyREQ.java", "modifyREQ.ftl");
        System.out.println(classInfo.getClassName() + " modifyREQ 生成成功！！！");
    }

    public static void genAddREQ(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", REQ_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, apiGenDir + REQ_DIR, classInfo.getClassName() + "AddREQ.java", "addREQ.ftl");
        System.out.println(classInfo.getClassName() + " addREQ 生成成功！！！");
    }

    public static void genMapper(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", MAPPER_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, serviceGenDir + MAPPER_DIR, classInfo.getClassName() + "Mapper.java", "mapper.ftl");
        System.out.println(classInfo.getClassName() + " mapper 生成成功！！！");
    }

    public static void genEntity(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", ENTITY_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, serviceGenDir + ENTITY_DIR, classInfo.getClassName() + ".java", "plusentity.ftl");
        System.out.println(classInfo.getClassName() + " entity 生成成功！！！");
    }

    public static void genService(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", SERVICE_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, serviceGenDir + SERVICE_DIR, classInfo.getClassName() + "Service.java", "service.ftl");
        System.out.println(classInfo.getClassName() + " service 生成成功！！！");
    }

    public static void genController(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", CONTROLLER_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, serviceGenDir + CONTROLLER_DIR, classInfo.getClassName() + "Controller.java", "controller.ftl");
        System.out.println(classInfo.getClassName() + " controller 生成成功！！！");
    }

    public static void genApi(String tableSql, Map<String, Object> params, ClassInfo classInfo) throws TemplateException, IOException {
        params.put("packageName", API_DIR.substring(1).replaceAll("\\/", "."));
        GeneratorUtil.generateCode(tableSql, params, apiGenDir + API_DIR, classInfo.getClassName() + "Api.java", "api.ftl");
        System.out.println(classInfo.getClassName() + " api 生成成功！！！");
    }

}
