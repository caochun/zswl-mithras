package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.core.lang.Assert;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.celleditor.MultiColumnNumberCellEditor;
import cn.zswltech.mithras.service.excel.model.StampDutyExcelModel;
import cn.zswltech.mithras.service.others.MithrasException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author luyujie
 * @date 2026/1/24
 * @description
 */
@Component
public class StampDutyExcelImporter extends AbstractSimpleExcelImporter<StampDutyExcelModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        //return new ExcelConfig(0, 1, null);
/*        ExcelConfig excelConfig = new ExcelConfig();*/
        List<List<Object>> all = excelReader.read();
        if (CollectionUtils.isEmpty(all)) {
            throw new MithrasException("导入的文件无数据");
        }
//        }
        return new ExcelConfig(0, 1, null);
    }

    @Override
    public List<StampDutyExcelModel> parse(InputStream inputStream) {
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        List<Object> firstRow = excelReader.readRow(0);
        String[] list = {"申报税目名称","合同编号/融资编号","客户名称/融资机构","业务部门","借据编号","实际起租日","不含税租金","不含税手续费","不含税咨询费","金额","印花税率（%）","印花税"};
        if(firstRow.size()<12){
            throw new MithrasException("请上传正确的印花税明细模板！");
        }
        for (int i = 0; i < firstRow.size(); i++) {
            Object cellValue = firstRow.get(i);
            if (Objects.nonNull(cellValue) && !Objects.equals(list[i], cellValue.toString())) {
                throw new MithrasException("请上传正确的印花税明细模板！");
            }
        }
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
            return excelReader.read(excelConfig.getHeaderRowIndex(), excelConfig.getDataStartRowIndex(), this.modelClz());
        } else {
            return excelReader.read(excelConfig.getHeaderRowIndex(), excelConfig.getDataStartRowIndex(), excelConfig.getDataEndRowIndex(), this.modelClz());
        }
    }

    @Override
    protected Class<StampDutyExcelModel> modelClz() {
        return StampDutyExcelModel.class;
    }

    @Override
    public Map<String, String> getHeaderAlias() {
        Map<String, String> headerMap = super.getHeaderAlias();

        return headerMap;
    }

    @Override
    protected CellEditor getCellEditor() {
        Map<String, MultiColumnNumberCellEditor.RoundingRule> columnRuleMap = new HashMap<>();
        columnRuleMap.put("印花税率（%）",  new MultiColumnNumberCellEditor.RoundingRule(3));
        return new MultiColumnNumberCellEditor(columnRuleMap);
    }

}
