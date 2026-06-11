package cn.zswltech.mithras.foundation.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateUtils {
    public static <T, R> void update(List<T> oldList, List<T> newList, Function<T, R> equalFunction,
                                     Consumer<List<T>> updateConsumer,
                                     Consumer<List<T>> deleteConsumer,
                                     Consumer<List<T>> addConsumer) {
        // 交集 需要更新的
        List<T> update = newList.stream()
                .filter(newItem -> oldList.stream().anyMatch(old -> Objects.equals(equalFunction.apply(old), equalFunction.apply(newItem))))
                .collect(Collectors.toList());
        // 差集 需要删除的
        List<T> delete = oldList.stream()
                .filter(old -> newList.stream().noneMatch(newItem -> Objects.equals(equalFunction.apply(old), equalFunction.apply(newItem))))
                .collect(Collectors.toList());
        // 差集 需要添加的
        List<T> add = newList.stream()
                .filter(newItem -> oldList.stream().noneMatch(old -> Objects.equals(equalFunction.apply(old), equalFunction.apply(newItem))))
                .collect(Collectors.toList());
        if (!update.isEmpty()) {
            // 更新
            updateConsumer.accept(update);
        }
        if (!delete.isEmpty()) {
            // 删除
            deleteConsumer.accept(delete);
        }
        if (!add.isEmpty()) {
            // 添加
            addConsumer.accept(add);
        }
    }
}
