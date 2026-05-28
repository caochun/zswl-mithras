package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationPayModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectPlanModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardOperationPayExcelExporter extends AbstractSimpleExcelExporter<DashboardOperationPayModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardOperationPayModel> modelClz() {
        return DashboardOperationPayModel.class;
    }
}
