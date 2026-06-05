package cn.zswltech.mithras.riskcontrol.strategy;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.InterceptRsp;
import cn.zswltech.mithras.dto.riskcontrol.PaymentApplyInterceptReq;
import cn.zswltech.mithras.dto.riskcontrol.ProjEstablishInterceptReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyCalReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyDetailReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyDetailRsp;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyListReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyListRsp;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyModifyReq;

public interface RiskControlStrategyApplicationService {

    void modify(RiskControlStrategyModifyReq req);

    PageR<RiskControlStrategyListRsp> list(RiskControlStrategyListReq req);

    RiskControlStrategyDetailRsp detail(RiskControlStrategyDetailReq req);

    InterceptRsp projEstablishIntercept(ProjEstablishInterceptReq req);

    InterceptRsp paymentApplyIntercept(PaymentApplyInterceptReq req);

    void recalculate(RiskControlStrategyCalReq req);

    void test(String metricCode);
}
