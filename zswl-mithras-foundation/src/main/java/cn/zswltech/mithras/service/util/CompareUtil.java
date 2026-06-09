package cn.zswltech.mithras.service.util;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.annotation.NotCompareColumn;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 对比
 *
 * @author wangchuanhao
 * @date 2022/6/30 11:57 PM
 */
public class CompareUtil {

    public static final String MODULE_CHANGED_FLAG_KEY = "moduleChangedFlag";

    @SneakyThrows
    public static <T> Map<String, DiffValue> compare(T t1, T t2, Set<String> ignoreFieldNames) {
        if (Objects.isNull(t2)) {
            return generateNew(t1);
        }
        Map<String, Field> fieldMap = new HashMap<>();
        collectField(t1.getClass(), fieldMap);
        Map<String, DiffValue> resultMap = new HashMap<>(fieldMap.size());

        for (String fieldName : fieldMap.keySet()) {
            if (CollectionUtils.isNotEmpty(ignoreFieldNames) && ignoreFieldNames.contains(fieldName)) {
                continue;
            }
            Field field = fieldMap.get(fieldName);
            ReflectionUtils.makeAccessible(field);
            Object t1Val = field.get(t1);
            Object t2Val = field.get(t2);
            boolean changed = !Objects.equals(t1Val, t2Val);
            if (changed && !resultMap.containsKey(MODULE_CHANGED_FLAG_KEY)) {
                resultMap.put(MODULE_CHANGED_FLAG_KEY, new DiffValue());
            }
            resultMap.put(fieldName, DiffValue.builder()
                    .value(t1Val)
                    .beforeValue(t2Val)
                    .isChange(changed)
                    .build());
        }
        return resultMap;
    }

    /**
     * 比较字符串之间差异
     * {@link DiffMatchPatch.Operation}
     **/
    @SneakyThrows
    public static LinkedList<DiffMatchPatch.Diff> compareString(String t1, String t2) {
        if (Objects.isNull(t1)) {
            LinkedList<DiffMatchPatch.Diff> diffs = new LinkedList<>();
            if (ObjectUtil.isNotEmpty(t2)) {
                diffs.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.INSERT, t2));
            }
            return diffs;
        }
        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        return diffMatchPatch.diffMain(t1, t2);
    }

    public static LinkedList<DiffMatchPatch.Diff> compareStringIgnoreExclude(String t1, String t2, char[] exclude) {
        String t1New = t1;
        String t2New = t2;
        if (Objects.nonNull(exclude) && exclude.length > 0) {
            t1New = StrUtil.removeAll(t1New, exclude);
            t2New = StrUtil.removeAll(t2New, exclude);
        }
        return compareString(t1New, t2New);
    }

    /**
     * 差异字符串使用标签包裹
     **/
    @SneakyThrows
    public static String string2HtmlString(LinkedList<DiffMatchPatch.Diff> diffs, String p1, String p2) {
        StringBuilder sb = new StringBuilder();
        diffs.forEach(diff -> {
            if (DiffMatchPatch.Operation.EQUAL.equals(diff.operation)) {
                sb.append(diff.text);
            } else if (DiffMatchPatch.Operation.INSERT.equals(diff.operation)) {
                sb.append(p1);
                sb.append(diff.text);
                sb.append(p2);
            }
        });
        return sb.toString();
    }

    /**
     * 比较
     *
     * @param t1
     * @param t2
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> Map<String, DiffValue> compare(T t1, T t2) {
        return compare(t1, t2, null);
    }

    /**
     * 转化
     *
     * @param t
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> Map<String, DiffValue> generateNew(T t) {
        Map<String, Field> fieldMap = new HashMap<>();
        collectField(t.getClass(), fieldMap);
        Map<String, DiffValue> resultMap = new HashMap<>(fieldMap.size());

        for (String fieldName : fieldMap.keySet()) {
            Field field = fieldMap.get(fieldName);
            ReflectionUtils.makeAccessible(field);
            Object val = field.get(t);
            resultMap.put(fieldName, DiffValue.builder()
                    .value(val)
                    .beforeValue(null)
                    .isChange(true)
                    .build());
        }
        resultMap.put(MODULE_CHANGED_FLAG_KEY, new DiffValue());
        return resultMap;
    }

    /**
     * 判断是否发生实际变动
     *
     * @param t1
     * @param t2
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> ChangeDTO checkActualChange(T t1, T t2) {
        ChangeDTO changeDTO = new ChangeDTO();
        Map<String, Field> fieldMap = new HashMap<>();
        collectField(t1.getClass(), fieldMap);
        for (String fieldName : fieldMap.keySet()) {
            Field field = fieldMap.get(fieldName);
            NotCompareColumn notCompareColumn = field.getAnnotation(NotCompareColumn.class);
            if (notCompareColumn != null && NotCompareColumn.IgnoreLevel.IGNORE.equals(notCompareColumn.ignoreLevel())) {
                continue;
            }
            ReflectionUtils.makeAccessible(field);
            Object t1Val = field.get(t1);
            Object t2Val = field.get(t2);
            if (!Objects.equals(t1Val, t2Val)) {
                changeDTO.setChangeFlag(true);
                if (notCompareColumn == null || !NotCompareColumn.IgnoreLevel.NOT_APPROVAL.equals(notCompareColumn.ignoreLevel())) {
                    // 如果该字段需要审批 已是最坏情况 快速返回
                    changeDTO.setNeedApprovalChangeFlag(true);
                    return changeDTO;
                } else {
                    // 如果被标记为不需要审批 则还不能返回 需要找到最坏情况
                    continue;
                }
            }

        }
        return changeDTO;
    }

    private static void collectField(Class<?> tableClass, Map<String, Field> fieldMap) {
        if (fieldMap == null) {
            fieldMap = new HashMap<>();
        }
        if (tableClass.equals(Object.class)) {
            return;
        }
        // 先找父类的字段，如有同名，子类覆盖父类
        Class<?> superClass = tableClass.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && !Map.class.isAssignableFrom(superClass)
                && !Collection.class.isAssignableFrom(superClass)) {
            collectField(tableClass.getSuperclass(), fieldMap);
        }
        // 找本类的字段
        Field[] fields = tableClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            //排除静态字段
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldMap.put(field.getName(), field);
            }
        }
    }

}