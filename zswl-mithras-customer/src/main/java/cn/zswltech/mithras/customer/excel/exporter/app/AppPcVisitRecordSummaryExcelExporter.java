package cn.zswltech.mithras.customer.excel.exporter.app;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.customer.excel.model.app.AppPcVisitRecordDetailModel;
import cn.zswltech.mithras.customer.excel.model.app.AppPcVisitRecordSummaryModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class AppPcVisitRecordSummaryExcelExporter extends AbstractSimpleExcelExporter<AppPcVisitRecordSummaryModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<AppPcVisitRecordSummaryModel> modelClz() {
        return AppPcVisitRecordSummaryModel.class;
    }
}
