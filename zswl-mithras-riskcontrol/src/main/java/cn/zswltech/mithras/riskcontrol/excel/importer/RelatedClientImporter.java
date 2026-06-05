package cn.zswltech.mithras.riskcontrol.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.riskcontrol.excel.model.RelatedClientExcelModel;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/8 15:55
 */
@Component
public class RelatedClientImporter extends AbstractSimpleExcelImporter<RelatedClientExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<RelatedClientExcelModel> modelClz() {
        return RelatedClientExcelModel.class;
    }
}
