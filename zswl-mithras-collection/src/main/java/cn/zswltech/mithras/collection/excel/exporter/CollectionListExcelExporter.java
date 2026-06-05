package cn.zswltech.mithras.collection.excel.exporter;

import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.collection.excel.model.CollectionListExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 收款核销列表
 *
 * @author wangchuanhao
 * @date 2022/8/23 3:31 PM
 */
@Component
public class CollectionListExcelExporter extends AbstractSimpleExcelExporter<CollectionListExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected void customStrategyExcelWriterBefore(ExcelWriter excelWriter) {
        LinkedHashMap<String, String> headerAlias = this.getHeaderAliasMap();
        List<String> headerNameList = new ArrayList<>(headerAlias.values());
        // 这行代码没啥用 只是为了占位
        excelWriter.writeHeadRow(Arrays.asList("基本信息", "状态", "计划收款", "实收情况"));
        excelWriter.writeSecHeadRow(headerNameList);

        excelWriter.merge(0, 0, 0, 2, "基本信息", true);
        excelWriter.merge(0, 1, 3, 3, "状态", true);
        excelWriter.merge(0, 0, 4, 7, "计划收款", true);
        excelWriter.merge(0, 0, 8, 9, "实收情况", true);
    }

    @Override
    protected Class<CollectionListExcelModel> modelClz() {
        return CollectionListExcelModel.class;
    }

    @Override
    protected boolean writeHead() {
        return false;
    }

    @Override
    protected int startRow() {
        return 2;
    }
}
