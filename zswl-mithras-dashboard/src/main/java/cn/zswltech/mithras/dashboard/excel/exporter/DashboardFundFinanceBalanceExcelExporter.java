package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceBalanceModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardFundFinanceBalanceExcelExporter extends AbstractSimpleExcelExporter<DashboardFundFinanceBalanceModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardFundFinanceBalanceModel> modelClz() {
        return DashboardFundFinanceBalanceModel.class;
    }
}
