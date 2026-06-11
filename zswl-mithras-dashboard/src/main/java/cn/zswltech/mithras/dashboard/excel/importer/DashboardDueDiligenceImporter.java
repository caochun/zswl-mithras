package cn.zswltech.mithras.dashboard.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardAdjustPersonModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardDueDiligenceModel;
import cn.zswltech.mithras.foundation.excel.celleditor.NumberToBigDecimalCellEditor;
import org.springframework.stereotype.Component;

@Component
public class DashboardDueDiligenceImporter extends AbstractSimpleExcelImporter<DashboardDueDiligenceModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return new NumberToBigDecimalCellEditor();
    }

    @Override
    protected Class<DashboardDueDiligenceModel> modelClz() {
        return DashboardDueDiligenceModel.class;
    }
}
