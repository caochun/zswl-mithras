package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPayModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectPayExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectPayModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectPayModel> modelClz() {
        return DashboardProjectPayModel.class;
    }
}
