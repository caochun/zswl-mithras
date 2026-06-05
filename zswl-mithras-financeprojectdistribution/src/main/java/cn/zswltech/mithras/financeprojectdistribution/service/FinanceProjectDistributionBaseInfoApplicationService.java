package cn.zswltech.mithras.financeprojectdistribution.service;

import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseREQ;

public interface FinanceProjectDistributionBaseInfoApplicationService {

    FinanceProjectDistributionBaseInfoRSP detail(FinanceProjectDistributionBaseREQ req);
}
