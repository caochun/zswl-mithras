package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationPayModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationTimeModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardOperationTimeExcelExporter extends AbstractSimpleExcelExporter<DashboardOperationTimeModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardOperationTimeModel> modelClz() {
        return DashboardOperationTimeModel.class;
    }
}
