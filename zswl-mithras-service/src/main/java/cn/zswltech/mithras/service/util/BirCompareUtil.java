package cn.zswltech.mithras.service.util;

import cn.zswltech.mithras.common.annotation.BirCompareColumn;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 客户工商信息对比
 * @Author: huangping
 * @Date: 2025/11/26  10:35
 * @Version: 1.0
 */
public class BirCompareUtil {


    /**
     * 判断是否发生实际变动
     *
     * @param t1        对象
     * @param t2        对象2
     * @param compareLv 工商登记 这里需要要限制 仅能传客户3个等级，
     * @return
     */
    @SneakyThrows
    public static <T> ChangeDTO checkActualChange(T t1, T t2, BirCompareColumn.CompareLv compareLv) {
        ChangeDTO changeDTO = new ChangeDTO();
        Map<String, Field> fieldMap = collectField(t1.getClass());
        for (String fieldName : fieldMap.keySet()) {
            Field field = fieldMap.get(fieldName);
            BirCompareColumn birCompareColumn = field.getAnnotation(BirCompareColumn.class);
            //未标记 跳过
            if (birCompareColumn == null) {
                continue;
            }
            //直接跳过--这个优先级最大
            if (BirCompareColumn.CompareLv.SKIP.equals(birCompareColumn.comparLv())) {
                continue;
            }
            ReflectionUtils.makeAccessible(field);
            Object t1Val = field.get(t1);
            Object t2Val = field.get(t2);
            if (Objects.equals(t1Val, t2Val)) {
                continue;
            }
            changeDTO.setChangeFlag(true);
            //等级大于 设置 说明需要校验
            if (compareLv.getSort() >= birCompareColumn.comparLv().getSort()) {
                // 如果该字段需要审批 已是最坏情况 快速返回
                changeDTO.setNeedApprovalChangeFlag(true);
                return changeDTO;
            }
        }
        return changeDTO;
    }


    /**
     * @param tableClass 目标类
     * @return 字段名->字段的映射（子类字段覆盖父类）
     */
    public static Map<String, Field> collectField(Class<?> tableClass) {
        Map<String, Field> fieldMap = new HashMap<>();
        collectField(tableClass, fieldMap);
        return fieldMap;
    }

    /**
     * 递归收集目标类及其父类的非静态字段（子类同名字段覆盖父类）
     *
     * @param tableClass 目标类（不可为null）
     * @param fieldMap   存储字段的容器（可为null，自动初始化）
     * @throws NullPointerException 若tableClass为null，提前暴露错误
     */
    private static void collectField(Class<?> tableClass, Map<String, Field> fieldMap) {
        // 1. 前置校验：禁止核心参数为null，异常信息更明确
        Objects.requireNonNull(tableClass, "收集字段的目标类[tableClass]不能为null");
        Map<String, Field> targetMap = fieldMap == null ? new HashMap<>() : fieldMap;
        // 3. 递归终止条件：已递归到Object类，直接返回
        if (Object.class.equals(tableClass)) {
            return;
        }
        // 4. 递归收集父类字段（过滤容器类父类，避免收集无效字段）
        Class<?> superClass = tableClass.getSuperclass();
        if (isValidSuperClass(superClass)) {
            collectField(superClass, targetMap);
        }
        for (Field field : tableClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                targetMap.put(field.getName(), field);
            }
        }
        // 6. 回写初始化后的容器（关键：避免外部传入null时拿不到结果）
        if (fieldMap == null) {
            fieldMap = targetMap;
        }
    }

    /**
     * 辅助方法：判断父类是否需要递归收集（排除Object、Map/Collection及其子类）
     */
    private static boolean isValidSuperClass(Class<?> superClass) {
        return superClass != null && !Object.class.equals(superClass) && !Map.class.isAssignableFrom(superClass) && !Collection.class.isAssignableFrom(superClass);
    }


}
