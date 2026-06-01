package cn.zswltech.mithras.service.overdue.domain;


import cn.zswltech.mithras.service.overdue.domain.share.Aggregate;
import cn.zswltech.mithras.service.overdue.domain.share.Identifier;
import cn.zswltech.mithras.service.overdue.domain.share.Repository;
import cn.zswltech.mithras.service.overdue.domain.share.diff.EntityDiff;
import lombok.AccessLevel;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 14:40
 */
// 这个类是一个通用的支撑类，为了减少开发者的重复劳动。在用的时候需要继承这个类
public abstract class DbRepositorySupport<T extends Aggregate<ID>, ID extends Identifier> implements Repository<T, ID> {

    @Getter
    private final Class<T> targetClass;

    // 让AggregateManager去维护Snapshot
    @Getter(AccessLevel.PROTECTED)
    private final AggregateManager<T, ID> aggregateManager;

    protected DbRepositorySupport(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.aggregateManager = AggregateManager.newInstance(targetClass);
    }

    /**
     * 这几个方法是继承的子类应该去实现的
     */
    protected abstract ID onInsert(T aggregate);

    protected abstract T onSelect(ID id);

    protected abstract void onUpdate(T aggregate, EntityDiff diff);

    protected abstract void onDelete(ID id);

    /**
     * Attach的操作就是让Aggregate可以被追踪
     */
    @Override
    public void attach(@NotNull T aggregate) {
        this.aggregateManager.attach(aggregate);
    }

    /**
     * Detach的操作就是让Aggregate停止追踪
     */
    @Override
    public void detach(@NotNull ID id) {
        this.aggregateManager.detach(id);
    }

    @Override
    public T find(@NotNull ID id) {
        T aggregate = this.onSelect(id);
        if (aggregate != null) {
            // 这里的就是让查询出来的对象能够被追踪。
            // 如果自己实现了一个定制查询接口，要记得单独调用attach。
            this.attach(aggregate);
        }
        return aggregate;
    }

    @Override
    public T findInCache(@NotNull ID id) {
        return this.aggregateManager.find(id);
    }

    @Override
    public void remove(@NotNull ID id) {
        this.onDelete(id);
        // 删除停止追踪
        this.detach(id);
    }

    @Override
    public void save(@NotNull T aggregate) {
        // 如果没有ID，直接插入
        if (aggregate.getBizId() == null) {
            ID id = this.onInsert(aggregate);
            aggregate.createNewId(id);
            return;
        }

        // 做Diff
        EntityDiff diff = aggregateManager.detectChanges(aggregate);
        if (!diff.isSelfModified() && diff.isEmpty()) {
            return;
        }
        // 调用UPDATE
        this.onUpdate(aggregate, diff);
        detach(aggregate.getBizId());
    }

    @Override
    public ID create(T aggregate) {
        ID id = this.onInsert(aggregate);
        this.attach(aggregate);
        return id;
    }
}