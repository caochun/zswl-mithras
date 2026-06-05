package cn.zswltech.mithras.finance.service;

import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueReportBaseAddREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueReportBaseListREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueReportBaseRemoveREQ;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueReportBase;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface FinanceOverdueReportBaseApplicationService {

    Long add(FinanceOverdueReportBaseAddREQ req);

    Page<FinanceOverdueReportBase> list(FinanceOverdueReportBaseListREQ req);

    void close(FinanceOverdueReportBaseRemoveREQ req);
}
