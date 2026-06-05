package cn.zswltech.mithras.workflow.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.workflow.excel.model.TrackEventExcelExporter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 租赁物台账导出
 *
 */
@Component
public class TrackEventExcelManagerExporter extends AbstractSimpleExcelExporter<TrackEventExcelExporter> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<TrackEventExcelExporter> modelClz() {
        return TrackEventExcelExporter.class;
    }

    @Override
    protected boolean writeHead() {
        return true;
    }

    @Override
    protected int startRow() {
        return 1;
    }
}
