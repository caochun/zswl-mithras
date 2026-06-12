package cn.zswltech.mithras.kpi.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.kpi.excel.model.EclExecuteRecordExcelModel;
import org.springframework.stereotype.Component;

@Component
public class EclExecuteRecordImporter extends AbstractSimpleExcelImporter<EclExecuteRecordExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<EclExecuteRecordExcelModel> modelClz() {
        return EclExecuteRecordExcelModel.class;
    }
}
