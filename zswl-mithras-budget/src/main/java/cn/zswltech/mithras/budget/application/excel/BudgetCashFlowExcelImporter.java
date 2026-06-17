package cn.zswltech.mithras.budget.application.excel;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.foundation.excel.celleditor.NumberToBigDecimalCellEditor;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class BudgetCashFlowExcelImporter extends AbstractSimpleExcelImporter<BudgetCashFlowExcelModel> {

    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        ExcelConfig excelConfig = new ExcelConfig();
        List<List<Object>> all = excelReader.read();
        if (CollectionUtils.isEmpty(all)) {
            throw new MithrasException("导入的文件无数据");
        }
        for (int i = 0; i < all.size(); i++) {
            List<Object> row = all.get(i);
            for (Object cellValue : row) {
                if (Objects.nonNull(cellValue) && Objects.equals("租金偿还方案", cellValue.toString())) {
                    excelConfig.setHeaderRowIndex(i);
                    int maxIndex = all.size() - 1;
                    if (i + 1 > maxIndex) {
                        throw new MithrasException("没有成功找到数据");
                    }
                    excelConfig.setDataStartRowIndex(i + 1);
                }
            }
        }
        return excelConfig;
    }

    @Override
    protected Class<BudgetCashFlowExcelModel> modelClz() {
        return BudgetCashFlowExcelModel.class;
    }

    @Override
    public Map<String, String> getHeaderAlias() {
        Map<String, String> headerMap = super.getHeaderAlias();
        headerMap.put("应收保理款（元）", "rent");
        headerMap.put("回收款（元）", "rent");
        return headerMap;
    }

    @Override
    protected CellEditor getCellEditor() {
        return new NumberToBigDecimalCellEditor();
    }
}
