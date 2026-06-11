package cn.zswltech.mithras.contract.overdue.application.collection;

import cn.zswltech.mithras.contract.overdue.model.OverdueCollection;

import java.util.Collection;
import java.util.List;

public interface CollectionOverdueHistoryResolver {

    List<OverdueCollection> overdueCollections(Collection<Long> clientIds);
}
