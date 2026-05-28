package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationCapacityModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationPayModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardOperationCapacityExcelExporter extends AbstractSimpleExcelExporter<DashboardOperationCapacityModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardOperationCapacityModel> modelClz() {
        return DashboardOperationCapacityModel.class;
    }
}
