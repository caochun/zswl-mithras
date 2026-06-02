package cn.zswltech.mithras.service.overdue.domain.litigation;

import cn.zswltech.mithras.service.overdue.domain.DbRepositorySupport;
import cn.zswltech.mithras.service.overdue.domain.collection.Collection;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectionAction;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectionActionId;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectionId;

/**
 * @description: 依赖反转，domain层定义的抽象类，在infrastructure层实现
 * @author: zhaozhengkang
 * @date: 2024/10/22 17:18
 */
public abstract class LitigationRepository extends DbRepositorySupport<Litigation, LongId> {
    protected LitigationRepository(Class<Litigation> targetClass) {
        super(targetClass);
    }
}
