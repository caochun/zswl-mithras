package cn.zswltech.mithras.application.orchestration.adapter.finance;

import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.finance.service.accountage.FinanceAccountAgeSupportPort;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2AccountAgeAddREQ;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class FinanceAccountAgeSupportPortAdapter implements FinanceAccountAgeSupportPort {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;

    @Override
    public List<CollectionBaseInfo> listCollections(Collection<Long> collectionIds) {
        return collectionBaseInfoService.listByIds(collectionIds);
    }

    @Override
    public Map<Long, Long> sumRemainingPrincipalByCollection(Collection<Long> collectionIds, LocalDate collectionDate) {
        return collectionBaseInfoService.sumRemainingPrincipalByCollection(collectionIds, collectionDate);
    }

    @Override
    public void sendAccountAge(List<CQ2AccountAgeAddREQ> reqs) {
        financialManagerServiceImpl2.sendAccountAge(reqs);
    }
}
