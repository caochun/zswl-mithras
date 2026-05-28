package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectSettleInThreeMonthModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectSettleInThreeMonthExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectSettleInThreeMonthModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectSettleInThreeMonthModel> modelClz() {
        return DashboardProjectSettleInThreeMonthModel.class;
    }
}
