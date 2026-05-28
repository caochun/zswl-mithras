package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectPayModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectPlanModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectPlanExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectPlanModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectPlanModel> modelClz() {
        return DashboardProjectPlanModel.class;
    }
}
