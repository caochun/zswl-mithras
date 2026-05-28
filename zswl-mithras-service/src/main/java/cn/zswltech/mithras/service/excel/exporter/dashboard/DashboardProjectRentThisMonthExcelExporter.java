package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectRentThisMonthModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectRentThisMonthExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectRentThisMonthModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectRentThisMonthModel> modelClz() {
        return DashboardProjectRentThisMonthModel.class;
    }
}
