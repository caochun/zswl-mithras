package cn.zswltech.mithras.service.overdue.domain;

import cn.zswltech.mithras.service.overdue.domain.share.Aggregate;
import cn.zswltech.mithras.service.overdue.domain.share.Identifier;
import cn.zswltech.mithras.service.overdue.domain.share.diff.EntityDiff;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 14:40
 */
public class GlobalAggregateManager<T extends Aggregate<ID>, ID extends Identifier> implements AggregateManager<T, ID> {

    private final DbContext<T, ID> context;
    private final Class<? extends T> targetClass;

    public GlobalAggregateManager(Class<? extends T> targetClass) {
        this.targetClass = targetClass;
        this.context = new DbContext<>(targetClass);
    }

    @Override
    public void attach(T aggregate) {
        context.attach(aggregate);
    }

    @Override
    public void attach(T aggregate, ID id) {
        context.setId(aggregate, id);
        context.attach(aggregate);
    }

    @Override
    public void detach(ID id) {
        context.detach(id);
    }

    @Override
    public T find(ID id) {
        return context.find(id);
    }

    @Override
    public EntityDiff detectChanges(T aggregate) {
        return context.detectChanges(aggregate);
    }

    @Override
    public void merge(T aggregate) {
        context.merge(aggregate);
    }
}
