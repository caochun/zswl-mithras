package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationConversionModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationTimeModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardOperationConversionExcelExporter extends AbstractSimpleExcelExporter<DashboardOperationConversionModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardOperationConversionModel> modelClz() {
        return DashboardOperationConversionModel.class;
    }
}
