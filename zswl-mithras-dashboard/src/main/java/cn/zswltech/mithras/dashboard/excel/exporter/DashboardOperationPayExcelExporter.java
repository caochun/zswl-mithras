package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationPayModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPlanModel;
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
