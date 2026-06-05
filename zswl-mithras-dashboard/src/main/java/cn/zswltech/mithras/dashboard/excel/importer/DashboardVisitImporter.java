package cn.zswltech.mithras.dashboard.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.celleditor.NumberToBigDecimalCellEditor;
import cn.zswltech.mithras.dashboard.excel.model.DashboardVisitModel;
import org.springframework.stereotype.Component;

@Component
public class DashboardVisitImporter extends AbstractSimpleExcelImporter<DashboardVisitModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<DashboardVisitModel> modelClz() {
        return DashboardVisitModel.class;
    }
}
