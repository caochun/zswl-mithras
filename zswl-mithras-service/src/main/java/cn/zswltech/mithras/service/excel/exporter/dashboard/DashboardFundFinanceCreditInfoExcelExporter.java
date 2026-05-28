package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardFundFinanceCreditInfoModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardFundFinanceCreditInfoExcelExporter extends AbstractSimpleExcelExporter<DashboardFundFinanceCreditInfoModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardFundFinanceCreditInfoModel> modelClz() {
        return DashboardFundFinanceCreditInfoModel.class;
    }
}
