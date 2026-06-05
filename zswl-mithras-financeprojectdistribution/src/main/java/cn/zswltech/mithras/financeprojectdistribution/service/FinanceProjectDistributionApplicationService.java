package cn.zswltech.mithras.financeprojectdistribution.service;

import cn.zswltech.mithras.api.common.R;

public interface FinanceProjectDistributionApplicationService {

    R<String> submit(Long projectDistributionId, boolean updateFlag);
}
