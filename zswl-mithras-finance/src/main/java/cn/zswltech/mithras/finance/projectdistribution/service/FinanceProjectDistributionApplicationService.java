package cn.zswltech.mithras.finance.projectdistribution.service;

import cn.zswltech.mithras.api.common.R;

public interface FinanceProjectDistributionApplicationService {

    R<String> submit(Long projectDistributionId, boolean updateFlag);
}
