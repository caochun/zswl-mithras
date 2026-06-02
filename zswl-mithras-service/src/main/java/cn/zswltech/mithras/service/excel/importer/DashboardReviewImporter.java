package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardReviewModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardVisitModel;
import cn.zswltech.mithras.ftp.newftp.excel.NumberToBigDecimalCellEditor;
import org.springframework.stereotype.Component;

@Component
public class DashboardReviewImporter extends AbstractSimpleExcelImporter<DashboardReviewModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return new NumberToBigDecimalCellEditor();
    }

    @Override
    protected Class<DashboardReviewModel> modelClz() {
        return DashboardReviewModel.class;
    }
}
