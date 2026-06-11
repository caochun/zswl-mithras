package cn.zswltech.mithras.budget.application.excel;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelImporter;
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
