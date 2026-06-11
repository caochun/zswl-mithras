package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.dto.liquiditymanage.financingrepay.FinancingRepayPlanModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.financingrepay.FinancingRepayWriteOffModifyREQ;

public interface FundRepayApplicationService {

    void planModify(FinancingRepayPlanModifyREQ req);

    void writeOffModify(FinancingRepayWriteOffModifyREQ req);
}
