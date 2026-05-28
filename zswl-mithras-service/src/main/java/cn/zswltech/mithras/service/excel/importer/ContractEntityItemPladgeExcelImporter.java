package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.celleditor.NumberToBigDecimalCellEditor;
import cn.zswltech.mithras.service.excel.model.ContractEntityPledgeItemExcelModel;
import cn.zswltech.mithras.service.others.MithrasException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
@Component
public class ContractEntityItemPladgeExcelImporter extends AbstractSimpleExcelImporter<ContractEntityPledgeItemExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        // 剔除可能存在的合计项
        ExcelConfig excelConfig = new ExcelConfig();
        excelConfig.setHeaderRowIndex(0);
        excelConfig.setDataStartRowIndex(1);
        List<List<Object>> all = excelReader.read();
        if (CollectionUtils.isEmpty(all)) {
            throw new MithrasException("导入的文件无数据");
        }
        for (int i = 0; i < all.size(); i++) {
            List<Object> row = all.get(i);
            for (Object cellValue : row) {
                if (Objects.nonNull(cellValue) && Objects.equals("合计", cellValue.toString())) {
                    excelConfig.setDataEndRowIndex(i - 1);
                    return excelConfig;
                }
            }
        }
        return excelConfig;
    }

    @Override
    protected Class<ContractEntityPledgeItemExcelModel> modelClz() {
        return ContractEntityPledgeItemExcelModel.class;
    }

    @Override
    public Map<String, String> getHeaderAlias() {
        Map<String, String> headerMap = super.getHeaderAlias();
        headerMap.put("评估价值（元）", "assessedValue");
        return headerMap;
    }

    @Override
    protected CellEditor getCellEditor() {
        return new NumberToBigDecimalCellEditor();
    }
}
