package cn.zswltech.mithras.report.util;

import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 比较工具类
 *
 * @author wangchuanhao
 * @date 2022/10/8 2:25 PM
 */
public class ReportCompareUtil {

    @SneakyThrows
    public static <T> boolean checkChange(T t1, T t2, Set<String> ignoreFieldNames) {
        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(t1.getClass());
        for (String fieldName : fieldMap.keySet()) {
            if (ignoreFieldNames.contains(fieldName)) {
                continue;
            }
            Field field = fieldMap.get(fieldName);
            ReflectionUtils.makeAccessible(field);
            Object t1Val = field.get(t1);
            Object t2Val = field.get(t2);
            if (t1Val == null && t2Val == null) {
                continue;
            }
            if (t1Val == null || t2Val == null) {
                return true;
            }
            if (!Objects.equals(t1Val, t2Val)) {
                return true;
            }
        }
        return false;
    }

}
