package cn.zswltech.mithras.kpi.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.kpi.excel.model.KpiProjGuessContractDetailExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName KpiProjGuessContractListExcelExporter
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/7/4 3:34 下午
 * @Version 1.0
 **/
@Component
public class KpiProjGuessContractDetailExcelExporter extends AbstractSimpleExcelExporter<KpiProjGuessContractDetailExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Map<Integer, Integer> getColumnsWide() {
        HashMap<Integer, Integer> wideMap = new HashMap<>();
        wideMap.put(1, 40);
        wideMap.put(2, 40);
        wideMap.put(6, 40);
        wideMap.put(11, 40);
        return wideMap;
    }

    @Override
    protected Class<KpiProjGuessContractDetailExcelModel> modelClz() {
        return KpiProjGuessContractDetailExcelModel.class;
    }

    //绩效-导出合并
    @Override
    public void exportExcel(List<KpiProjGuessContractDetailExcelModel> dataList, OutputStream outputStream) {
        super.exportExcel(dataList, outputStream);
        /*LinkedHashMap<String, String> headerAlias = this.getHeaderAliasMap();
        List<String> headerNameList = new ArrayList<>(headerAlias.values());
        //添加子模块信息
        headerNameList.add("人员/部门");
        headerNameList.add("人员/部门名称");
        headerNameList.add("占比%");
        headerNameList.add("分润-当期值");
        headerNameList.add("分润-累计值");
        headerNameList.add("项目奖金-当期值");
        headerNameList.add("项目奖金-累计值");
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        Map<Integer, Integer> columnsWideMap = getColumnsWide();
        //Map<Integer, CellStyle> columnStyleMap = this.getColumnStyleMap(excelWriter.getWorkbook());
        this.customStrategyExcelWriterBefore(excelWriter);
        if (writeHead()) {
            excelWriter.writeHeadRow(headerNameList);
        }
        List<KpiProjGuessContractDetailExcelModel.ModelBody> bodies;
        for (int i = 1, j = 0; j < dataList.size(); j++) {
            KpiProjGuessContractDetailExcelModel rowData = dataList.get(j);
            bodies = rowData.getBodies();
            if (ObjectUtil.isNotEmpty(bodies)) {
                if (bodies.size() > 1) {
                    excelWriter.merge(i, i + bodies.size() - 1, 0, 0, rowData.getCalculateDate(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 1, 1, rowData.getContractCode(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 2, 2, rowData.getProjName(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 3, 3, rowData.getProjSource(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 4, 4, rowData.getProjClassify(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 5, 5, rowData.getContractStartDate(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 6, 6, rowData.getBelongDeptName(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 7, 7, rowData.getAwardRatio(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 8, 8, rowData.getProfitCurrent(), false);
                    excelWriter.merge(i, i + bodies.size() - 1, 9, 9, rowData.getProfitTotal(), false);
                } else {
                    excelWriter.writeCellValue(0, i, rowData.getCalculateDate());
                    excelWriter.writeCellValue(1, i, rowData.getContractCode());
                    excelWriter.writeCellValue(2, i, rowData.getProjName());
                    excelWriter.writeCellValue(3, i, rowData.getProjSource());
                    excelWriter.writeCellValue(4, i, rowData.getProjClassify());
                    excelWriter.writeCellValue(5, i, rowData.getContractStartDate());
                    excelWriter.writeCellValue(6, i, rowData.getBelongDeptName());
                    excelWriter.writeCellValue(7, i, rowData.getAwardRatio());
                    excelWriter.writeCellValue(8, i, rowData.getProfitCurrent());
                    excelWriter.writeCellValue(9, i, rowData.getProfitTotal());

                }
            }
            // 写单元格
            if (ObjectUtil.isEmpty(bodies)) {
                i++;
            } else {
                for (KpiProjGuessContractDetailExcelModel.ModelBody bodie : bodies) {
                    excelWriter.writeCellValue(10, i, bodie.getDivideTypeName());
                    excelWriter.writeCellValue(11, i, bodie.getDivideTargetName());
                    excelWriter.writeCellValue(12, i, bodie.getWeightValue());
                    excelWriter.writeCellValue(13, i, bodie.getProfitCurrent());
                    excelWriter.writeCellValue(14, i, bodie.getProfitTotal());
                    excelWriter.writeCellValue(15, i, bodie.getBonusCurrent());
                    excelWriter.writeCellValue(16, i, bodie.getBonusTotal());
                    *//*if (Objects.nonNull(columnStyleMap)) {
                        CellStyle cellStyle = columnStyleMap.get(i);
                        if (Objects.nonNull(cellStyle)) {
                            for (int y = 10; y <= 16; y++) {
                                excelWriter.setStyle(cellStyle, y, i);
                            }
                        }
                    }*//*
                    i++;
                }
            }
            for (int y = 0; y <= 16; y++) {
                excelWriter.setColumnWidth(y, columnsWideMap.getOrDefault(y, 20));
            }
        }
        // 写入到输出流之前给子类一次自定义更改的机会
        this.customStrategy(excelWriter.getWorkbook());
        // 写入到输出流
        excelWriter.flush(outputStream, true);*/
    }

}
