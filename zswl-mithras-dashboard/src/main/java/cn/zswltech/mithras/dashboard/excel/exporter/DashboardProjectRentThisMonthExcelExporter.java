package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectRentThisMonthModel;
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
