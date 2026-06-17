package cn.zswltech.mithras.collection.overdue.history;

import java.util.Collection;
import java.util.List;

public interface CollectionOverdueHistoryQueryService {

    List<CollectionOverdueHistorySnapshot> overdueCollections(Collection<Long> clientIds);
}
