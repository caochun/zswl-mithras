package cn.zswltech.mithras.liquidity.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.foundation.excel.celleditor.NumberToBigDecimalCellEditor;
import cn.zswltech.mithras.liquidity.excel.model.AccountBalanceBaseInfoExcelModel;
import org.springframework.stereotype.Component;

/**
 *  账户余额表导入
 * @author chenyifei
 * @since 2024/12/23
 */
@Component
public class AccountBalanceBaseInfoImporter extends AbstractSimpleExcelImporter<AccountBalanceBaseInfoExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return new NumberToBigDecimalCellEditor();
    }

    @Override
    protected Class<AccountBalanceBaseInfoExcelModel> modelClz() {
        return AccountBalanceBaseInfoExcelModel.class;
    }
}
