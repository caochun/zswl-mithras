package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.budget.application.excel.EclExecutePredictRecordExcelModel;
import org.springframework.stereotype.Component;

@Component
public class EclExecutePredictRecordImporter extends AbstractSimpleExcelImporter<EclExecutePredictRecordExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<EclExecutePredictRecordExcelModel> modelClz() {
        return EclExecutePredictRecordExcelModel.class;
    }
}
