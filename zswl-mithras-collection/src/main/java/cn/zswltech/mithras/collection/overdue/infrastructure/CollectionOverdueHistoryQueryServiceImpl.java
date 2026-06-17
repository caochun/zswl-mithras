package cn.zswltech.mithras.collection.overdue.infrastructure;

import cn.zswltech.mithras.collection.mapper.CollectionOverdueHistoryMapper;
import cn.zswltech.mithras.collection.overdue.history.CollectionOverdueHistoryQueryService;
import cn.zswltech.mithras.collection.overdue.history.CollectionOverdueHistorySnapshot;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Component
public class CollectionOverdueHistoryQueryServiceImpl implements CollectionOverdueHistoryQueryService {

    @Resource
    private CollectionOverdueHistoryMapper collectionOverdueHistoryMapper;

    @Override
    public List<CollectionOverdueHistorySnapshot> overdueCollections(Collection<Long> clientIds) {
        return collectionOverdueHistoryMapper.getOverdueCollection(clientIds);
    }
}
