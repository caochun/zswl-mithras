package cn.zswltech.mithras.riskcontrol.relation;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface RiskControlRelatedClientApplicationService {

    void importFile(InputStream inputStream);

    PageR<RiskControlRelatedTransactionRsp> paymentList(RiskControlRelatedTransactionPageReq req);

    PageR<RiskControlRelatedTransactionRsp> collectionList(RiskControlRelatedTransactionPageReq req);

    Map<String, List<String>> pullDown();
}
