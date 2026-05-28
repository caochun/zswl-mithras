package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardAdjustPersonModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardDueDiligenceModel;
import cn.zswltech.mithras.service.service.newftp.excel.NumberToBigDecimalCellEditor;
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
