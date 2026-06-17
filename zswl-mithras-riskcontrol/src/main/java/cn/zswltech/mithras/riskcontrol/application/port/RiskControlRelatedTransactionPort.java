package cn.zswltech.mithras.riskcontrol.application.port;

import cn.zswltech.mithras.api.common.PageR;

public interface RiskControlRelatedTransactionPort {

    PageR<RiskControlRelatedTransactionFact> paymentTransactions(RiskControlRelatedTransactionQuery query);

    PageR<RiskControlRelatedTransactionFact> collectionTransactions(RiskControlRelatedTransactionQuery query);
}
