package cn.zswltech.mithras.service.overdue.domain.collection;

import cn.zswltech.mithras.service.overdue.domain.DbRepositorySupport;

/**
 * @description: 依赖反转，domain层定义的抽象类，在infrastructure层实现
 * @author: zhaozhengkang
 * @date: 2024/10/22 17:18
 */
public abstract class CollectionRepository extends DbRepositorySupport<Collection, CollectionId> {
    protected CollectionRepository(Class<Collection> targetClass) {
        super(targetClass);
    }

    /**
     * 查询指定催收动作
     * @param id
     * @return
     */
    public abstract CollectionAction findAction(CollectionActionId id);

    public abstract CollectionAction findActionLib(CollectionActionId id, String version);

    public abstract Integer findCollectLetterIndex(int year);

    public abstract void incrementCollectLetterIndex(int year, int count);

    public abstract Long addAction(CollectionAction dto);

    public abstract void updateAction(CollectionAction action);

    public abstract void deleteAction(CollectionActionId id);
}
