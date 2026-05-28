package cn.zswltech.mithras.service.excel;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
public abstract class AbstractSimpleExcelImporter<T extends ExcelModel> implements SimpleExcelImporter<T> {
    public static final int EXCEL_ROW_LIMIT = 15000;

    @Override
    public List<T> parse(InputStream inputStream) {
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        Assert.isTrue(excelReader.getRowCount() <= EXCEL_ROW_LIMIT, () -> MithrasException.newException("最多支持导入" + EXCEL_ROW_LIMIT + "行数据"));
        Map<String, String> headerAlias = Assert.notEmpty(this.getHeaderAlias(), () -> MithrasException.newException("未定义表头数据映射"));
        excelReader.setHeaderAlias(headerAlias);
        CellEditor cellEditor = this.getCellEditor();
        if (Objects.nonNull(cellEditor)) {
            excelReader.setCellEditor(cellEditor);
        }
        // 解析excel获取表头、首行数据和末行数据
        ExcelConfig excelConfig = this.config(excelReader);
        // 校验必要信息
        if (Objects.isNull(excelConfig.getHeaderRowIndex()) || Objects.isNull(excelConfig.getDataStartRowIndex())) {
            throw new MithrasException("没有从文件中获取到必要数据，请检查文件是否符合导入模板");
        }
        if (Objects.isNull(excelConfig.getDataEndRowIndex())) {
            return excelReader.read(excelConfig.headerRowIndex, excelConfig.dataStartRowIndex, this.modelClz());
        } else {
            return excelReader.read(excelConfig.headerRowIndex, excelConfig.dataStartRowIndex, excelConfig.dataEndRowIndex, this.modelClz());
        }
    }

    protected abstract ExcelConfig config(ExcelReader excelReader);

    protected Map<String, String> getHeaderAlias() {
        Class<T> clz = this.modelClz();
        Field[] fields = ReflectUtil.getFields(clz);
        if (Objects.isNull(fields) || fields.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, String> headerMap = new HashMap<>();
        for (Field field : fields) {
            SimpleExcelHeader simpleExcelHeader = field.getAnnotation(SimpleExcelHeader.class);
            if (Objects.isNull(simpleExcelHeader)) {
                continue;
            }
            String headerName = simpleExcelHeader.headerName();
            if (StrUtil.isBlank(headerName)) {
                continue;
            }
            headerMap.put(headerName, field.getName());
        }
        return headerMap;
    }

    protected abstract CellEditor getCellEditor();

    protected abstract Class<T> modelClz();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    protected static class ExcelConfig {
        /**
         * 表头行索引
         */
        private Integer headerRowIndex;
        /**
         * 首行数据索引
         */
        private Integer dataStartRowIndex;
        /**
         * 末行数据索引
         */
        private Integer dataEndRowIndex;
    }
}
