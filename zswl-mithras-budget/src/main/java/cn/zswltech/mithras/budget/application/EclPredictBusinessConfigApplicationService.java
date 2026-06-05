package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.EclPredictBusinessConfigDetailREQ;
import cn.zswltech.mithras.dto.budget.EclPredictBusinessConfigDetailRSP;
import cn.zswltech.mithras.dto.budget.EclPredictBusinessConfigListREQ;
import cn.zswltech.mithras.dto.budget.EclPredictBusinessConfigListRSP;
import cn.zswltech.mithras.dto.budget.EclPredictBusinessConfigModifyREQ;

public interface EclPredictBusinessConfigApplicationService {

    void modify(EclPredictBusinessConfigModifyREQ req);

    PageR<EclPredictBusinessConfigListRSP> pageList(EclPredictBusinessConfigListREQ req);

    EclPredictBusinessConfigDetailRSP detailRsp(EclPredictBusinessConfigDetailREQ req);
}
