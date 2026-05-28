package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardClientAfterLeaseCheckModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardClientOverviewAllExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class DashboardClientAfterLeaseCheckExcelExporter extends AbstractSimpleExcelExporter<DashboardClientAfterLeaseCheckModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardClientAfterLeaseCheckModel> modelClz() {
        return DashboardClientAfterLeaseCheckModel.class;
    }
}
