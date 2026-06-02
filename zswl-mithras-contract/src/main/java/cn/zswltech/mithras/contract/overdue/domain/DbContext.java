package cn.zswltech.mithras.contract.overdue.domain;

import cn.zswltech.mithras.contract.overdue.domain.share.Aggregate;
import cn.zswltech.mithras.contract.overdue.domain.share.Identifier;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.DiffUtils;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.EntityDiff;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.ReflectionUtils;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.SnapshotUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 14:42
 */
public class DbContext<T extends Aggregate<ID>, ID extends Identifier> {

    @Getter
    private Class<? extends T> aggregateClass;


    private final Cache<ID, T> aggregateMap = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(24, TimeUnit.HOURS).build();

    public DbContext(Class<? extends T> aggregateClass) {
        this.aggregateClass = aggregateClass;
    }

    public void attach(T aggregate) {
        if (aggregate.getBizId() != null) {
            // 缓存中没有该对象，或者版本不一致，则更新缓存
            if (!aggregateMap.asMap().containsKey(aggregate.getBizId())) {
                this.merge(aggregate);
            } else {
                if (!Objects.equals(aggregate.getLockVersion(),
                        aggregateMap.getIfPresent(aggregate.getBizId()).getLockVersion())) {
                    this.merge(aggregate);
                }
            }
        }
    }

    public void detach(ID id) {
        if (id != null) {
            aggregateMap.invalidate(id);
        }
    }

    public EntityDiff detectChanges(T aggregate) {
        if (aggregate.getBizId() == null) {
            return EntityDiff.EMPTY;
        }
        T snapshot = aggregateMap.getIfPresent(aggregate.getBizId());
        if (snapshot == null) {
            attach(aggregate);
        }
        return DiffUtils.diff(snapshot, aggregate);
    }

    public T find(ID id) {
        return aggregateMap.getIfPresent(id);
    }

    public void merge(T aggregate) {
        if (aggregate.getBizId() != null && !aggregate.getBizId().isNull()) {
            T snapshot = SnapshotUtils.snapshot(aggregate);
            aggregateMap.put(aggregate.getBizId(), snapshot);
        }
    }

    public void setId(T aggregate, ID id) {
        ReflectionUtils.writeField("id", aggregate, id);
    }
}
