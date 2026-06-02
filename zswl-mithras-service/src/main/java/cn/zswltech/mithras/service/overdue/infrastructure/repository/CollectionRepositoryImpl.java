package cn.zswltech.mithras.service.overdue.infrastructure.repository;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.contract.overdue.domain.collection.*;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.Diff;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.DiffType;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.EntityDiff;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.ListDiff;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.OverdueCollectionActionDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.OverdueCollectionActionLibDao;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.OverdueCollectionDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollection;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollectionAction;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollectionActionLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectionActionConverter;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectionConverter;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 17:10
 */
@Component
public class CollectionRepositoryImpl extends CollectionRepository {

    @Resource
    private OverdueCollectionDao overdueCollectionDao;
    @Resource
    private OverdueCollectionActionLibDao overdueCollectionActionLibDao;
    @Resource
    private OverdueCollectionActionDao overdueCollectionActionDao;
    @Resource
    private CollectionConverter collectionConverter;
    @Resource
    private CollectionActionConverter collectionActionConverter;
    @Resource
    private Id2NameService id2NameService;


    public CollectionRepositoryImpl() {
        super(Collection.class);
    }

    @Override
    protected CollectionId onInsert(Collection aggregate) {
        OverdueCollection overdueCollection = collectionConverter.entity2Po(aggregate);
        overdueCollectionDao.save(overdueCollection);
        return new CollectionId(overdueCollection.getId());
    }

    @Override
    protected Collection onSelect(CollectionId collectionId) {
        OverdueCollection aggregatePo = overdueCollectionDao.getById(collectionId.getId());
        if (aggregatePo == null) {
            throw new MithrasException("未找到该记录");
        }
        List<OverdueCollectionAction> actionPos = overdueCollectionActionDao.list(Wrappers.<OverdueCollectionAction>lambdaQuery()
                .eq(OverdueCollectionAction::getOcId, collectionId.getId())
                .orderByDesc(OverdueCollectionAction::getId));
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(actionPos.stream().map(BaseModel::getCreateBy).collect(Collectors.toSet()));
        Collection collection = collectionConverter.po2Entity(aggregatePo);
        if (ObjectUtil.isNotEmpty(actionPos)) {
            List<CollectionAction> collectionActions = collectionActionConverter.po2Entity(actionPos);
            collectionActions.forEach(
                    action -> {
                        action.setProcessPerson(userId2Name.get(action.getCreateBy()));
                    }
            );
            collection.setCollectionActionList(collectionActions);
        }
        return collection;
    }

    @Override
    protected void onUpdate(Collection aggregate, EntityDiff diff) {
        if (diff.isSelfModified()) {
            OverdueCollection orderDO = collectionConverter.entity2Po(aggregate);
            overdueCollectionDao.updateById(orderDO);
        }
        Diff lineItemDiffs = diff.getDiff("collectionActionList");
        if (lineItemDiffs instanceof ListDiff) {
            ListDiff diffList = (ListDiff) lineItemDiffs;

            for (Diff itemDiff : diffList) {
                if (itemDiff.getType() == DiffType.Removed) {
                    CollectionAction actionEntity = (CollectionAction) itemDiff.getOldValue();
                    OverdueCollectionAction action = collectionActionConverter.entity2Po(actionEntity);
                    overdueCollectionActionDao.removeById(action.getId());
                }
                if (itemDiff.getType() == DiffType.Modified) {
                    CollectionAction actionEntity = (CollectionAction) itemDiff.getNewValue();
                    OverdueCollectionAction action = collectionActionConverter.entity2Po(actionEntity);
                    overdueCollectionActionDao.updateById(action);
                }
            }
        }
    }

    @Override
    protected void onDelete(CollectionId collectionId) {
        overdueCollectionActionDao.remove(Wrappers.<OverdueCollectionAction>lambdaQuery().eq(OverdueCollectionAction::getOcId, collectionId.getId()));
        overdueCollectionDao.removeById(collectionId.getId());
    }

    @Override
    public CollectionAction findAction(CollectionActionId id) {
        return collectionActionConverter.po2Entity(overdueCollectionActionDao.getById(id.getId()));
    }

    @Override
    public Integer findCollectLetterIndex(int year) {
        return overdueCollectionActionDao.findCollectLetterIndex(year);
    }

    @Override
    public void incrementCollectLetterIndex(int year, int count) {
        overdueCollectionActionDao.incrementCollectLetterIndex(year,count);
    }

    @Override
    public Long addAction(CollectionAction action) {
        OverdueCollectionAction overdueCollectionAction = collectionActionConverter.entity2Po(action);
        overdueCollectionActionDao.save(overdueCollectionAction);
        return overdueCollectionAction.getId();
    }

    @Override
    public void updateAction(CollectionAction action) {
        overdueCollectionActionDao.updateById(collectionActionConverter.entity2Po(action));
    }

    @Override
    public void deleteAction(CollectionActionId id) {
        overdueCollectionActionDao.removeById(id);
    }

    @Override
    public CollectionAction findActionLib(CollectionActionId id, String version) {
        OverdueCollectionActionLib oneLib = overdueCollectionActionLibDao.getOne(Wrappers.<OverdueCollectionActionLib>lambdaQuery()
                .eq(OverdueCollectionActionLib::getOriginId, id.getId())
                .eq(OverdueCollectionActionLib::getVersion, version)
                .last("limit 1"));
        return collectionActionConverter.libPo2Entity(oneLib);
    }
}
