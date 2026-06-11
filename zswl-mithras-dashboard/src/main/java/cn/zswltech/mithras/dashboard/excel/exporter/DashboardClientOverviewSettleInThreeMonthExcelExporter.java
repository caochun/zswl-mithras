package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewSettleThreeMonthExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardClientOverviewSettleInThreeMonthExcelExporter extends AbstractSimpleExcelExporter<DashboardClientOverviewSettleThreeMonthExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardClientOverviewSettleThreeMonthExcelModel> modelClz() {
        return DashboardClientOverviewSettleThreeMonthExcelModel.class;
    }
}
