package cn.zswltech.mithras.service.overdue.infrastructure;

import cn.zswltech.mithras.contract.overdue.application.collection.CollectionOverdueHistoryResolver;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollection;
import cn.zswltech.mithras.collection.mapper.CollectionOverdueHistoryMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Component
public class CollectionOverdueHistoryResolverImpl implements CollectionOverdueHistoryResolver {

    @Resource
    private CollectionOverdueHistoryMapper collectionOverdueHistoryMapper;

    @Override
    public List<OverdueCollection> overdueCollections(Collection<Long> clientIds) {
        return collectionOverdueHistoryMapper.getOverdueCollection(clientIds);
    }
}
