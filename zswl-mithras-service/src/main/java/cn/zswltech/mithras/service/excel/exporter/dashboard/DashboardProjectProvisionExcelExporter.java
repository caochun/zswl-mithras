package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectProvisionModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectProvisionExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectProvisionModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectProvisionModel> modelClz() {
        return DashboardProjectProvisionModel.class;
    }
}
