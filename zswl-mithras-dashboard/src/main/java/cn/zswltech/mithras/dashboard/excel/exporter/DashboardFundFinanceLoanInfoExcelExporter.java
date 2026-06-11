package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceLoanInfoModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardFundFinanceLoanInfoExcelExporter extends AbstractSimpleExcelExporter<DashboardFundFinanceLoanInfoModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardFundFinanceLoanInfoModel> modelClz() {
        return DashboardFundFinanceLoanInfoModel.class;
    }
}
