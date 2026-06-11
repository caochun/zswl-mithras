package cn.zswltech.mithras.customer.app.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.customer.app.excel.model.AppPcVisitRecordDetailModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class AppPcVisitRecordDetailExcelExporter extends AbstractSimpleExcelExporter<AppPcVisitRecordDetailModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<AppPcVisitRecordDetailModel> modelClz() {
        return AppPcVisitRecordDetailModel.class;
    }
}
