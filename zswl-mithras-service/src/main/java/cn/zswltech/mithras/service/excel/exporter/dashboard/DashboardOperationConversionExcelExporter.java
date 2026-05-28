package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationConversionModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationTimeModel;
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
