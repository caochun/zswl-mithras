package cn.zswltech.mithras.contract.overdue.domain;

import cn.zswltech.mithras.contract.overdue.domain.share.Aggregate;
import cn.zswltech.mithras.contract.overdue.domain.share.Identifier;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.EntityDiff;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 14:40
 */
public class ThreadLocalAggregateManager<T extends Aggregate<ID>, ID extends Identifier> implements AggregateManager<T, ID> {

    private final ThreadLocal<DbContext<T, ID>> context;
    private final Class<? extends T> targetClass;

    public ThreadLocalAggregateManager(Class<? extends T> targetClass) {
        this.targetClass = targetClass;
        this.context = ThreadLocal.withInitial(() -> new DbContext<>(targetClass));
    }

    @Override
    public void attach(T aggregate) {
        context.get().attach(aggregate);
    }

    @Override
    public void attach(T aggregate, ID id) {
        context.get().setId(aggregate, id);
        context.get().attach(aggregate);
    }

    @Override
    public void detach(ID id) {
        context.get().detach(id);
    }

    @Override
    public T find(ID id) {
        return context.get().find(id);
    }

    @Override
    public EntityDiff detectChanges(T aggregate) {
        return context.get().detectChanges(aggregate);
    }

    @Override
    public void merge(T aggregate) {
        context.get().merge(aggregate);
    }
}
