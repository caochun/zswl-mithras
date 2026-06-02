package cn.zswltech.mithras.contract.overdue.domain.share.diff;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.contract.overdue.domain.share.Entity;
import cn.zswltech.mithras.contract.overdue.domain.share.Identifiable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 16:10
 */
public class DiffUtils {
    public static <T> EntityDiff diff(T snapshot, T cur) {

        if (!(snapshot instanceof Entity)) {
            return EntityDiff.EMPTY;
        }
        EntityDiff result = new EntityDiff();
        Field[] fields = cur.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 当前字段的类型
            Class<?> curFieldClazz = field.getType();
            // 当前字段类型的接口类型列表
            Set<Class<?>> curFieldClazzInterfacesSet = new HashSet<>(Arrays.asList(curFieldClazz.getInterfaces()));
            if (curFieldClazz == List.class) {
                //处理子实体列表
                Type genericType = field.getGenericType();
                // 如果是泛型参数的类型
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    //得到泛型里的class类型对象
                    Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
                    Class<?>[] interfaces = genericClazz.getInterfaces();
                    if (ObjectUtil.isEmpty(interfaces)) {
                        continue;
                    }
                    Set<Class<?>> interfacesSet = new HashSet<>(Arrays.asList(genericClazz.getInterfaces()));
                    if (interfacesSet.contains(Entity.class)) {
                        ListDiff listDiff = new ListDiff();
                        List<Entity> curFieldValue = (List<Entity>) ReflectUtil.getFieldValue(cur, field.getName());
                        // id为空，说明要插入
                        curFieldValue
                                .stream()
                                .filter(v -> v.getBizId() == null || v.getBizId().isNull())
                                .forEach(v -> listDiff.addDiff(new Diff(DiffType.Added, null, v)));
                        // id不为空
                        Map<String, Entity> curMap = curFieldValue.stream()
                                .filter(v -> v.getBizId() != null  && !v.getBizId().isNull())
                                .collect(Collectors.toMap(Identifiable::bizIdString, v -> v));
                        List<Entity> snapshotFieldValue = (List<Entity>) ReflectUtil.getFieldValue(snapshot, field.getName());
                        if (snapshotFieldValue == null) {
                            snapshotFieldValue = new ArrayList<>();
                        }
                        Map<String, Entity> snapshotMap = snapshotFieldValue.stream()
                                .collect(Collectors.toMap(Identifiable::bizIdString, v -> v));

                        Set<String> handledIdSet = new HashSet<>();
                        for (Map.Entry<String, Entity> entry : curMap.entrySet()) {
                            String id = entry.getKey();
                            Entity curEntity = entry.getValue();
                            Entity snapshotEntity = snapshotMap.get(id);
                            if (snapshotEntity == null) {
                                listDiff.addDiff(new Diff(DiffType.Added, null, curEntity));
                            } else {
                                EntityDiff diff = diff(snapshotEntity, curEntity);
                                if (diff.isSelfModified()) {
                                    listDiff.addDiff(new Diff(DiffType.Modified, snapshotEntity, curEntity));
                                }
                                handledIdSet.add(id);
                            }
                        }
                        for (Map.Entry<String, Entity> entry : snapshotMap.entrySet()) {
                            if(!handledIdSet.contains(entry.getKey())) {
                                listDiff.addDiff(new Diff(DiffType.Removed, entry.getValue(), null));
                            }
                        }
                        if (!listDiff.isEmpty()) {
                            result.addDiff(field.getName(), listDiff);
                        }
                    }
                }
            } else if(curFieldClazzInterfacesSet.contains(Entity.class)){
                // 处理单子实体
                Entity curEntity = (Entity) ReflectUtil.getFieldValue(cur, field.getName());
                Entity snapshotEntity = (Entity) ReflectUtil.getFieldValue(snapshot, field.getName());
                if (snapshotEntity == null ) {
                    if(curEntity != null) {
                        // 快照空，当前不为空，说明新增
                        result.addDiff(field.getName(), new Diff(DiffType.Added, null, curEntity));
                    }
                } else {
                    if(curEntity != null) {
                        // 快照不为空，当前不为空，判断是否修改
                        EntityDiff diff = diff(snapshotEntity, curEntity);
                        if (diff.isSelfModified()) {
                            result.addDiff(field.getName(), new Diff(DiffType.Modified, snapshotEntity, curEntity));
                        }
                    }else {
                        // 快照不为空，当前为空，说明删除
                        result.addDiff(field.getName(), new Diff(DiffType.Removed, snapshotEntity, null));
                    }
                }
            } else {
                // 处理聚合根其他属性
                Object aggregateFieldValue = ReflectUtil.getFieldValue(cur, field.getName());
                Object snapshotegateFieldValue = ReflectUtil.getFieldValue(snapshot, field.getName());
                // 如果当前对象属性为空，默认为没有发生变更
                if(ObjectUtil.isEmpty(aggregateFieldValue)){
                    // todo: 如需将空值也视为变更，请修改此处代码
                    continue;
                }
                if (!Objects.equals(aggregateFieldValue, snapshotegateFieldValue)) {
                    result.setSelfModified(true);
//                    if (cur instanceof Entity) {
//                        Diff diff = new Diff(DiffType.Modified, snapshot, cur);
//                        result.addDiff(field.getName(), diff);
//                    }
                }
            }
        }
        return result;
    }
}
