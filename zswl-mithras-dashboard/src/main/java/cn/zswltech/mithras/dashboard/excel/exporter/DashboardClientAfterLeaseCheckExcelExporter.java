package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientAfterLeaseCheckModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewAllExcelModel;
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
