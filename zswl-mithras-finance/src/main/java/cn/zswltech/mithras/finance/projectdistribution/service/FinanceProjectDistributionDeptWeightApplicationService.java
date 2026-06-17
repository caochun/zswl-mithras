package cn.zswltech.mithras.finance.projectdistribution.service;

import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptWeightSaveREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionWeightREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionWeightRSP;

public interface FinanceProjectDistributionDeptWeightApplicationService {

    void saveDept(FinanceProjectDistributionDeptWeightSaveREQ req);

    FinanceProjectDistributionWeightRSP detail(FinanceProjectDistributionWeightREQ req);
}
