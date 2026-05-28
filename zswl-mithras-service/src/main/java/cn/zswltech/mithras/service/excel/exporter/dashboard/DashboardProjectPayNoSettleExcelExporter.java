package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectPayNoSettleModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectPayNoSettleExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectPayNoSettleModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectPayNoSettleModel> modelClz() {
        return DashboardProjectPayNoSettleModel.class;
    }
}
