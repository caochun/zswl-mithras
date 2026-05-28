package cn.zswltech.mithras.service.overdue.domain;


import cn.zswltech.mithras.service.overdue.domain.share.Aggregate;
import cn.zswltech.mithras.service.overdue.domain.share.Identifier;
import cn.zswltech.mithras.service.overdue.domain.share.diff.EntityDiff;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 15:02
 */
public interface AggregateManager<T extends Aggregate<ID>, ID extends Identifier> {
    void attach(T aggregate);

    void attach(T aggregate, ID id);

    void detach(ID id);

    T find(ID id);

    EntityDiff detectChanges(T aggregate);

    void merge(T aggregate);

    static <T extends Aggregate<ID>, ID extends Identifier> AggregateManager<T, ID>
    newInstance(Class<? extends T> targetClass) {
        return new GlobalAggregateManager<>(targetClass);
    }
}
