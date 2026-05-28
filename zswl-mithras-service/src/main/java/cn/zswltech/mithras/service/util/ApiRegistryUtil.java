package cn.zswltech.mithras.service.util;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author bigbear
 * @date 2024/10/10 14:20
 * @description 获取API功能注册SQL
 */
public class ApiRegistryUtil {

    private static final String API_SQL_TEMPLATE = "INSERT INTO bifrost_function (code, name, menu_id, method, path, type) VALUES";

    public static void main(String[] args) throws ClassNotFoundException {
        //System.out.println(exportApiSql("cn.zswltech.mithras.api.capital.CapitalWriteOffApi", 719));
        //System.out.println(exportApiSql("cn.zswltech.mithras.api.capital.BankFlowProcessingCenterFinanceApi", 719));
        //System.out.println(exportApiSql("cn.zswltech.mithras.api.contract.ContractAccountApi", 719));
        System.out.println(exportApiSql("cn.zswltech.mithras.api.dashboard.boss.BusinessMonthCollectApi", 727));
        System.out.println(exportApiSql("cn.zswltech.mithras.api.dashboard.boss.CurrentYearBusinessPayReceiptApi", 727));

    }

    public static String exportApiSql(String allClassName, Integer menuId) throws ClassNotFoundException{
        return exportApiSql(allClassName, menuId,  null);
    }

    public static String exportApiSql(String allClassName, Integer menuId, String menuCode) throws ClassNotFoundException {
        // 1、找到当前的API接口
        Class<?> clazz = Class.forName(allClassName);
        // root path
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        String rootPath = "";
        if (Objects.nonNull(requestMapping)) {
            if (ObjectUtil.isNotEmpty(requestMapping.path())) {
                rootPath = requestMapping.path()[0];
            } else {
                rootPath = requestMapping.value()[0];
            }
        }
        Method[] declaredMethods = clazz.getDeclaredMethods();
        StringBuilder sqlBuffer = new StringBuilder(API_SQL_TEMPLATE);
        for (Method declaredMethod : declaredMethods) {
            // 2、找到当前接口的注解
            ApiOperation apiOperation = declaredMethod.getAnnotation(ApiOperation.class);
            // 找到方法路径
            String methodPath = "";
            String methodType = "";
            PostMapping postMapping = declaredMethod.getAnnotation(PostMapping.class);
            GetMapping getMapping = declaredMethod.getAnnotation(GetMapping.class);
            DeleteMapping deleteMapping = declaredMethod.getAnnotation(DeleteMapping.class);
            PutMapping putMapping = declaredMethod.getAnnotation(PutMapping.class);
            if (postMapping != null) {
                if (ObjectUtil.isNotEmpty(postMapping.path())) {
                    methodPath = postMapping.path()[0];
                } else {
                    methodPath = postMapping.value()[0];
                }
                methodType = "POST";
            } else if (getMapping != null) {
                if (ObjectUtil.isNotEmpty(getMapping.path())) {
                    methodPath = getMapping.path()[0];
                } else {
                    methodPath = getMapping.value()[0];
                }
                methodType = "GET";
            } else if (deleteMapping != null) {
                if (ObjectUtil.isNotEmpty(deleteMapping.path())) {
                    methodPath = deleteMapping.path()[0];
                } else {
                    methodPath = deleteMapping.value()[0];
                }
                methodType = "DELETE";
            } else if (putMapping != null) {
                if (ObjectUtil.isNotEmpty(putMapping.path())) {
                    methodPath = putMapping.path()[0];
                } else {
                    methodPath = putMapping.value()[0];
                }
                methodType = "PUT";
            }
            if (apiOperation != null) {
                String path = rootPath + methodPath;
                StringBuilder code = new StringBuilder();
                for (int i = 1; i < path.length(); i++) {
                    if (path.charAt(i) == '/') {
                        i++;
                        char charredAt = path.charAt(i);
                        code.append(String.valueOf(charredAt).toUpperCase());
                    } else {
                        code.append(path.charAt(i));
                    }
                }
                // 3、拼接SQL
                sqlBuffer.append("('").append(code).append("','")
                        .append(apiOperation.value());

                if (ObjectUtil.isNotEmpty(menuCode)) {
                    sqlBuffer.append("',");
                    sqlBuffer.append("(")
                            .append("select id from bifrost_menu where code = '")
                            .append(menuCode)
                            .append("')");
                } else {
                    sqlBuffer.append("','");
                    sqlBuffer.append(menuId);
                }
                if (ObjectUtil.isNotEmpty(menuCode)) {
                    sqlBuffer.append(",'");
                } else {
                    sqlBuffer.append("','");
                }
                sqlBuffer.append(methodType)
                        .append("','").append(path)
                        .append("','").append(2).append("'),");
            }
        }
        return sqlBuffer.substring(0, sqlBuffer.length() - 1) + ";";
    }
}
