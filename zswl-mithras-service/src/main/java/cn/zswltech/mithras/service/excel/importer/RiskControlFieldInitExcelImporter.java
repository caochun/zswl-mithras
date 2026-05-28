package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.model.RiskControlFieldInitExcelModel;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/17 09:52
 */
@Component
public class RiskControlFieldInitExcelImporter extends AbstractSimpleExcelImporter<RiskControlFieldInitExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(2, 3, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<RiskControlFieldInitExcelModel> modelClz() {
        return RiskControlFieldInitExcelModel.class;
    }
}
