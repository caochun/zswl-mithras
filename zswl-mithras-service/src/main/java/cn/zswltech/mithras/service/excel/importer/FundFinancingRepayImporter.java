package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.celleditor.NumberToBigDecimalCellEditor;
import cn.zswltech.mithras.service.excel.model.FundFinancingRepayEstimateExcelModel;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingRepayImporter extends AbstractSimpleExcelImporter<FundFinancingRepayEstimateExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return new NumberToBigDecimalCellEditor();
    }

    @Override
    protected Class<FundFinancingRepayEstimateExcelModel> modelClz() {
        return FundFinancingRepayEstimateExcelModel.class;
    }
}
