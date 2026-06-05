package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewOverdueExcelModel;
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
