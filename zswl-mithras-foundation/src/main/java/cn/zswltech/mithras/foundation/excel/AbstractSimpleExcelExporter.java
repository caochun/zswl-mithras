package cn.zswltech.mithras.foundation.excel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author dingqi
 * @date 2022/8/19
 * @description
 */
@Slf4j
public abstract class AbstractSimpleExcelExporter<T extends ExcelModel> implements SimpleExcelExporter<T> {
    @Override
    public void exportExcel(List<T> dataList, OutputStream outputStream) {
        LinkedHashMap<String, String> headerAlias = this.getHeaderAliasMap();
        int columns = headerAlias.size();
        List<String> headerKeyList = new ArrayList<>(headerAlias.keySet());
        List<String> headerNameList = new ArrayList<>(headerAlias.values());
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        Map<Integer, Integer> columnsWideMap = getColumnsWide();
        Map<Integer, CellStyle> columnStyleMap = this.getColumnStyleMap(excelWriter.getWorkbook());
        this.customStrategyExcelWriterBefore(excelWriter);
        if (writeHead()) {
            excelWriter.writeHeadRow(headerNameList);
        }
        for (int i = 0; i < dataList.size(); i++) {
            int rowIndex = i + startRow();
            T rowData = dataList.get(i);
            for (int j = 0; j < columns; j++) {
                String key = headerKeyList.get(j);
                // 反射取值
                Object value = ReflectUtil.getFieldValue(rowData, key);
                // 写单元格
                excelWriter.writeCellValue(j, rowIndex, value);
                // 覆盖样式
                excelWriter.setColumnWidth(j, columnsWideMap.getOrDefault(j, 20));
                if (Objects.nonNull(columnStyleMap)) {
                    CellStyle cellStyle = columnStyleMap.get(j);
                    if (Objects.nonNull(cellStyle)) {
                        excelWriter.setStyle(cellStyle, j, rowIndex);
                    }
                }
            }
        }
        // 写入到输出流之前给子类一次自定义更改的机会
        this.customStrategy(excelWriter.getWorkbook());
        // 写入到输出流
        excelWriter.flush(outputStream, true);
    }

    protected LinkedHashMap<String, String> getHeaderAliasMap() {
        List<Field> fields = this.listOrderFields();
        if (CollectionUtil.isEmpty(fields)) {
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

    protected boolean writeHead() {
        return true;
    }

    protected int startRow() {
        return 1;
    }

    protected Map<Integer, Integer> getColumnsWide(){
        return new HashMap<>();
    }

    protected Map<Integer, CellStyle> getColumnStyleMap(Workbook workbook) {
        List<Field> fields = this.listOrderFields();
        if (CollectionUtil.isEmpty(fields)) {
            return null;
        }
        Map<Integer, CellStyle> columnMap = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            SimpleExcelHeader simpleExcelHeader = fields.get(i).getAnnotation(SimpleExcelHeader.class);
            if (Objects.isNull(simpleExcelHeader)) {
                continue;
            }
            ColumnStyleEnum columnStyleEnum = simpleExcelHeader.columnStyle();
            switch (columnStyleEnum) {
                case DATE: {
                    columnMap.put(i, MyStyleUtil.createMyDateCellStyle(workbook));
                    break;
                }
                case MONEY: {
                    columnMap.put(i, MyStyleUtil.createMyMoneyCellStyle(workbook));
                }
            }
        }
        return columnMap;
    }

    protected abstract void customStrategy(Workbook workbook);

    protected void customStrategyExcelWriterBefore(ExcelWriter excelWriter) {}

    protected abstract Class<T> modelClz();

    private List<Field> listOrderFields() {
        Class<T> clz = this.modelClz();
        Field[] fields = ReflectUtil.getFields(clz);
        if (Objects.isNull(fields) || fields.length == 0) {
            return null;
        }
        // 去掉没有注解的字段
        List<Field> filterFields = new LinkedList<>();
        for (Field field : fields) {
            if (Objects.isNull(field.getAnnotation(SimpleExcelHeader.class))) {
                continue;
            }
            filterFields.add(field);
        }
        // 先排序
        filterFields.sort((o1, o2) -> {
            SimpleExcelHeader seh1 = o1.getAnnotation(SimpleExcelHeader.class);
            SimpleExcelHeader seh2 = o2.getAnnotation(SimpleExcelHeader.class);
            int i1 = Optional.ofNullable(seh1).map(SimpleExcelHeader::headerOrder).orElse(0);
            int i2 = Optional.ofNullable(seh2).map(SimpleExcelHeader::headerOrder).orElse(0);
            return i1 - i2;
        });
        return filterFields;
    }
}
