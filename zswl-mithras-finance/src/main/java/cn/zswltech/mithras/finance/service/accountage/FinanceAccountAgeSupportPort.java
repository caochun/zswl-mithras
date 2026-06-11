package cn.zswltech.mithras.finance.service.accountage;

import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2AccountAgeAddREQ;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FinanceAccountAgeSupportPort {

    List<CollectionBaseInfo> listCollections(Collection<Long> collectionIds);

    Map<Long, Long> sumRemainingPrincipalByCollection(Collection<Long> collectionIds, LocalDate collectionDate);

    void sendAccountAge(List<CQ2AccountAgeAddREQ> reqs);
}
