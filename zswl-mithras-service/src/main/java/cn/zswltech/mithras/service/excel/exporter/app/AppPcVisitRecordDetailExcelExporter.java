package cn.zswltech.mithras.service.excel.exporter.app;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.app.AppPcVisitRecordDetailModel;
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
