package cn.zswltech.mithras.finance.service;

import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationListREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationModifyREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationRemoveREQ;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueIntegration;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface FinanceOverdueIntegrationApplicationService {

    void modify(FinanceOverdueIntegrationModifyREQ req);

    Page<FinanceOverdueIntegration> list(FinanceOverdueIntegrationListREQ req);

    void remove(FinanceOverdueIntegrationRemoveREQ req);
}
