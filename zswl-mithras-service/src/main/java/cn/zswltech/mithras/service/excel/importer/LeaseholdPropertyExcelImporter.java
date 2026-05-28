package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.model.LeaseholdPropertyExcelModel;
import org.springframework.stereotype.Component;

/**
 * @author yangxiong
 * @since 2023-08-14
 */
@Component
public class LeaseholdPropertyExcelImporter extends AbstractSimpleExcelImporter<LeaseholdPropertyExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<LeaseholdPropertyExcelModel> modelClz() {
        return LeaseholdPropertyExcelModel.class;
    }
}
