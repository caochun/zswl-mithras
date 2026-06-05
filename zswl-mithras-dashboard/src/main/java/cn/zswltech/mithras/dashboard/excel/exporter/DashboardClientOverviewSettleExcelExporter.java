package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewSettleModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardClientOverviewSettleExcelExporter extends AbstractSimpleExcelExporter<DashboardClientOverviewSettleModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardClientOverviewSettleModel> modelClz() {
        return DashboardClientOverviewSettleModel.class;
    }
}
