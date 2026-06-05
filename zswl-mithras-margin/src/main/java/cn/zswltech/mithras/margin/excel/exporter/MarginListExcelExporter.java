package cn.zswltech.mithras.margin.excel.exporter;

import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.margin.excel.model.MarginBaseInfoListExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 合同收付款 列表
 *
 * @author wangchuanhao
 * @date 2022/8/22 4:12 PM
 */
@Component
public class MarginListExcelExporter extends AbstractSimpleExcelExporter<MarginBaseInfoListExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected void customStrategyExcelWriterBefore(ExcelWriter excelWriter) {
        LinkedHashMap<String, String> headerAlias = this.getHeaderAliasMap();
        List<String> headerNameList = new ArrayList<>(headerAlias.values());
        // 这行代码没啥用 只是为了占位
        excelWriter.writeHeadRow(Arrays.asList("基本信息", "保证金收款", "保证金付款"));
        excelWriter.writeSecHeadRow(headerNameList);

        excelWriter.merge(0, 0, 0, 2, "基本信息", true);
        excelWriter.merge(0, 0, 3, 4, "保证金收款", true);
        excelWriter.merge(0, 0, 5, 7, "保证金付款", true);
    }

    @Override
    protected Class<MarginBaseInfoListExcelModel> modelClz() {
        return MarginBaseInfoListExcelModel.class;
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
