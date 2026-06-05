package cn.zswltech.mithras.collection.excel.exporter;

import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.collection.excel.model.ContractcpListExcelModel;
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
public class ContractcpListExcelExporter extends AbstractSimpleExcelExporter<ContractcpListExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected void customStrategyExcelWriterBefore(ExcelWriter excelWriter) {
        LinkedHashMap<String, String> headerAlias = this.getHeaderAliasMap();
        List<String> headerNameList = new ArrayList<>(headerAlias.values());
        // 这行代码没啥用 只是为了占位
        excelWriter.writeHeadRow(Arrays.asList("基本信息1", "基本信息2", "已收租金", "剩余金额"));
        excelWriter.writeSecHeadRow(headerNameList);

        excelWriter.merge(0, 0, 0, 3, "基本信息1", true);
        excelWriter.merge(0, 0, 4, 6, "基本信息2", true);
        excelWriter.merge(0, 0, 7, 9, "已收租金", true);
        excelWriter.merge(0, 0, 10, 13, "剩余金额", true);
//        excelWriter.merge(0, 0, 14, 17, "逾期情况", true);
    }

    @Override
    protected Class<ContractcpListExcelModel> modelClz() {
        return ContractcpListExcelModel.class;
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
