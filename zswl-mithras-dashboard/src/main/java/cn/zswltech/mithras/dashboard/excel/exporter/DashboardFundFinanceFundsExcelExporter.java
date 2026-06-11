package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceFundsModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardFundFinanceFundsExcelExporter extends AbstractSimpleExcelExporter<DashboardFundFinanceFundsModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardFundFinanceFundsModel> modelClz() {
        return DashboardFundFinanceFundsModel.class;
    }
}
