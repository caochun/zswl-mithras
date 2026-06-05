package cn.zswltech.mithras.liquidity.excel.exporter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.liquidity.excel.model.AssetsCashOutflowListExcelModel;
import cn.zswltech.mithras.liquidity.excel.model.FundsCashOutflowListExcelModel;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @create: 2023-05-17
 **/

@Component
public class CashOutflowListExcelExporter {

    public void exportExcel(List<FundsCashOutflowListExcelModel> sheetList1, List<AssetsCashOutflowListExcelModel> sheetList2, OutputStream outputStream) {
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        LinkedHashMap<String, String> headerAlias = this.getHeaderAliasMap(FundsCashOutflowListExcelModel.class);
        if (CollUtil.isNotEmpty(headerAlias)) {
            excelWriter.renameSheet("资金端");
            writeValue(sheetList1, excelWriter, headerAlias);
        }
        headerAlias = this.getHeaderAliasMap(AssetsCashOutflowListExcelModel.class);
        if (CollUtil.isNotEmpty(headerAlias)) {
            excelWriter.setSheet("资产端");
            writeValue(sheetList2, excelWriter, headerAlias);
        }
        // 写入到输出流
        excelWriter.flush(outputStream, true);
    }

    public<T> void writeValue(List<T> dataList,ExcelWriter excelWriter,LinkedHashMap<String, String> headerAlias){
        int columns = headerAlias.size();
        List<String> headerKeyList = new ArrayList<>(headerAlias.keySet());
        List<String> headerNameList = new ArrayList<>(headerAlias.values());
        excelWriter.writeHeadRow(headerNameList);
        if (CollUtil.isNotEmpty(dataList)) {
            for (int i = 0; i < dataList.size(); i++) {
                int rowIndex = i + 1;
                T rowData = dataList.get(i);
                for (int j = 0; j < columns; j++) {
                    String key = headerKeyList.get(j);
                    // 反射取值
                    Object value = ReflectUtil.getFieldValue(rowData, key);
                    // 写单元格
                    excelWriter.writeCellValue(j, rowIndex, value);
                    // 覆盖样式
                    excelWriter.setColumnWidth(j, 20);
                }
            }
        }
    }

    private<T> LinkedHashMap<String, String> getHeaderAliasMap(Class<T> clz) {
        Field[] fields = this.listOrderFields(clz);
        if (Objects.isNull(fields)) {
            return null;
        }
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        // 按顺序找到表头映射信息
        for (Field field : fields) {
            SimpleExcelHeader simpleExcelHeader = field.getAnnotation(SimpleExcelHeader.class);
            if (Objects.isNull(simpleExcelHeader)) {
                continue;
            }
            String headerName = simpleExcelHeader.headerName();
            if (StrUtil.isBlank(headerName)) {
                continue;
            }
            headerMap.put(field.getName(), headerName);
        }
        return headerMap;
    }
    private<T> Field[] listOrderFields(Class<T> clz) {
        Field[] fields = ReflectUtil.getFields(clz);
        if (Objects.isNull(fields) || fields.length == 0) {
            return null;
        }
        // 先排序
        Arrays.sort(fields, (o1, o2) -> {
            SimpleExcelHeader seh1 = o1.getAnnotation(SimpleExcelHeader.class);
            SimpleExcelHeader seh2 = o2.getAnnotation(SimpleExcelHeader.class);
            int i1 = Optional.ofNullable(seh1).map(SimpleExcelHeader::headerOrder).orElse(0);
            int i2 = Optional.ofNullable(seh2).map(SimpleExcelHeader::headerOrder).orElse(0);
            return i1 - i2;
        });
        return fields;
    }

}
