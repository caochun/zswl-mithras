package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewAllExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardClientOverviewAllExcelExporter extends AbstractSimpleExcelExporter<DashboardClientOverviewAllExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardClientOverviewAllExcelModel> modelClz() {
        return DashboardClientOverviewAllExcelModel.class;
    }
}
