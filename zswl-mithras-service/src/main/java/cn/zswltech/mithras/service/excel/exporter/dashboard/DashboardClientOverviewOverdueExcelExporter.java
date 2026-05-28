package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardClientOverviewOverdueExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardClientOverviewOverdueExcelExporter extends AbstractSimpleExcelExporter<DashboardClientOverviewOverdueExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardClientOverviewOverdueExcelModel> modelClz() {
        return DashboardClientOverviewOverdueExcelModel.class;
    }
}
