package cn.zswltech.mithras.customer.mobile.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.customer.mobile.excel.model.AppPcVisitRecordSummaryModel;
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
