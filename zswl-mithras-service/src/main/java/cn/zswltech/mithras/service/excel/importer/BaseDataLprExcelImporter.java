package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.model.BaseDataLprExcelModel;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@Component
public class BaseDataLprExcelImporter extends AbstractSimpleExcelImporter<BaseDataLprExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<BaseDataLprExcelModel> modelClz() {
        return BaseDataLprExcelModel.class;
    }
}
