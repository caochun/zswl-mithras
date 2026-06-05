package cn.zswltech.mithras.finance.service;

import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationPushRSP;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueVersionSubmitREQ;

public interface FinanceOverdueVersionApplicationService {

    void submit(FinanceOverdueVersionSubmitREQ req);

    FinanceOverdueIntegrationPushRSP push(FinanceOverdueVersionSubmitREQ req);
}
