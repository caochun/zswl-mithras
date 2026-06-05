package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPayModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPlanModel;
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
