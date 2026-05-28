package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardFundFinanceLoanInfoModel;
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
