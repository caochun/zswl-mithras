package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationPayModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationTimeModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardOperationTimeExcelExporter extends AbstractSimpleExcelExporter<DashboardOperationTimeModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardOperationTimeModel> modelClz() {
        return DashboardOperationTimeModel.class;
    }
}
