package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationCapacityModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationPayModel;
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
