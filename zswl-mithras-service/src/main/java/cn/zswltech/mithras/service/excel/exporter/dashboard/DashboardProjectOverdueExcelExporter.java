package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectOverdueModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectOverdueExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectOverdueModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectOverdueModel> modelClz() {
        return DashboardProjectOverdueModel.class;
    }
}
