package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.zswltech.mithras.collection.overdue.history.CollectionOverdueHistoryQueryService;
import cn.zswltech.mithras.collection.overdue.history.CollectionOverdueHistorySnapshot;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionOverdueHistoryResolver;
import cn.zswltech.mithras.contract.overdue.model.OverdueCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractCollectionOverdueHistoryResolverAdapter implements CollectionOverdueHistoryResolver {

    @Resource
    private CollectionOverdueHistoryQueryService collectionOverdueHistoryQueryService;

    @Override
    public List<OverdueCollection> overdueCollections(Collection<Long> clientIds) {
        return collectionOverdueHistoryQueryService.overdueCollections(clientIds)
                .stream()
                .map(this::toOverdueCollection)
                .collect(Collectors.toList());
    }

    private OverdueCollection toOverdueCollection(CollectionOverdueHistorySnapshot snapshot) {
        OverdueCollection overdueCollection = new OverdueCollection();
        overdueCollection.setClientId(snapshot.getClientId());
        overdueCollection.setOverdueRent(snapshot.getOverdueRent());
        overdueCollection.setLateCharge(snapshot.getLateCharge());
        overdueCollection.setCurMaxOverdueDays(snapshot.getCurMaxOverdueDays());
        return overdueCollection;
    }
}
