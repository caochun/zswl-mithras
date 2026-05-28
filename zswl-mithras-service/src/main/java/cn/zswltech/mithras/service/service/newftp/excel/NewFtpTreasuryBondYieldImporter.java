package cn.zswltech.mithras.service.service.newftp.excel;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class NewFtpTreasuryBondYieldImporter extends AbstractSimpleExcelImporter<NewFtpTreasuryBondYieldExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return new NumberToBigDecimalCellEditor();
    }

    @Override
    protected Class<NewFtpTreasuryBondYieldExcelModel> modelClz() {
        return NewFtpTreasuryBondYieldExcelModel.class;
    }
}
