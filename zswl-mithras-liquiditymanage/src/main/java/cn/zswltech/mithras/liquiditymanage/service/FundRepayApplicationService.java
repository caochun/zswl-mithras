package cn.zswltech.mithras.liquiditymanage.service;

import cn.zswltech.mithras.dto.liquiditymanage.financingRepay.FinancingRepayPlanModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.financingRepay.FinancingRepayWriteOffModifyREQ;

public interface FundRepayApplicationService {

    void planModify(FinancingRepayPlanModifyREQ req);

    void writeOffModify(FinancingRepayWriteOffModifyREQ req);
}
