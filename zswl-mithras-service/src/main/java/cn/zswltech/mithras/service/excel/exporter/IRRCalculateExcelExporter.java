package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.MyStyleUtil;
import cn.zswltech.mithras.service.excel.model.IRRCalculateExcelModel;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
@Component
public class IRRCalculateExcelExporter {
    public void export(OutputStream outputStream, IRRCalculateExcelModel excelModel) {
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        List<String> headerList = Arrays.asList("实际日期", "调整后日期", "实际期项", "调整后期项", "实际现金流", "折现后现金流");
        for (int i = 0; i < headerList.size(); i++) {
            excelWriter.setColumnWidth(i, 20);
        }
        // 写表头
        excelWriter.writeHeadRow(headerList);
        BigDecimal adjustCashFlowAmountSum = BigDecimal.ZERO;
        // 写入现金流表数据
        for (int i = 1; i <= excelModel.getCashFlowAdjustExcelModelList().size(); i++) {
            IRRCalculateExcelModel.CashFlowAdjustExcelModel model = excelModel.getCashFlowAdjustExcelModelList().get(i - 1);
            // 实际日期
            Cell cashFlowDateCell = excelWriter.getOrCreateCell(0, i);
            cashFlowDateCell.setCellStyle(MyStyleUtil.createMyDateCellStyle(excelWriter.getWorkbook()));
            cashFlowDateCell.setCellValue(model.getCashFlowDate());
            // 调整后日期
            Cell adjustCashFlowDateCell = excelWriter.getOrCreateCell(1, i);
            adjustCashFlowDateCell.setCellStyle(MyStyleUtil.createMyDateCellStyle(excelWriter.getWorkbook()));
            adjustCashFlowDateCell.setCellValue(model.getAdjustCashFlowDate());
            // 实际期项
            Cell cashFlowPhaseCell = excelWriter.getOrCreateCell(2, i);
            cashFlowPhaseCell.setCellStyle(MyStyleUtil.createDefaultCellStyle(excelWriter.getWorkbook()));
            if (Objects.nonNull(model.getCashFlowPhase())) {
                cashFlowPhaseCell.setCellValue(model.getCashFlowPhase());
            }
            // 调整后期项
            Cell adjustCashFlowPhaseCell = excelWriter.getOrCreateCell(3, i);
            adjustCashFlowPhaseCell.setCellStyle(MyStyleUtil.createDefaultCellStyle(excelWriter.getWorkbook()));
            if (Objects.nonNull(model.getAdjustCashFlowPhase())) {
                adjustCashFlowPhaseCell.setCellValue(model.getAdjustCashFlowPhase().setScale(9, RoundingMode.HALF_UP).doubleValue());
            }
            // 实际现金流
            Cell cashFlowAmountCell = excelWriter.getOrCreateCell(4, i);
            cashFlowAmountCell.setCellStyle(MyStyleUtil.createMyMoneyCellStyle(excelWriter.getWorkbook()));
            cashFlowAmountCell.setCellValue(model.getCashFlowAmount().divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 15, RoundingMode.HALF_UP).doubleValue());
            // 调整后现金流
            adjustCashFlowAmountSum = adjustCashFlowAmountSum.add(model.getAdjustCashFlowAmount());
            Cell adjustCashFlowAmountCell = excelWriter.getOrCreateCell(5, i);
            adjustCashFlowAmountCell.setCellStyle(MyStyleUtil.createMyMoneyCellStyle(excelWriter.getWorkbook()));
            adjustCashFlowAmountCell.setCellValue(model.getAdjustCashFlowAmount().divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 15, RoundingMode.HALF_UP).doubleValue());
        }
        int currentRow = excelModel.getCashFlowAdjustExcelModelList().size() + 1;
        // 写入合计数据
        excelWriter.writeCellValue(4, currentRow, "净现值NPV");
        Cell adjustCashFlowAmountSumCell = excelWriter.getOrCreateCell(5, currentRow);
        adjustCashFlowAmountSumCell.setCellStyle(MyStyleUtil.createMyMoneyCellStyle(excelWriter.getWorkbook()));
        adjustCashFlowAmountSumCell.setCellValue(adjustCashFlowAmountSum.doubleValue());
        // 写入IRR信息
        currentRow = currentRow + 2;
        excelWriter.writeCellValue(4, currentRow, "irr");
        Cell irrPerPhase = excelWriter.getOrCreateCell(5, currentRow);
        irrPerPhase.setCellStyle(MyStyleUtil.createDefaultCellStyle(excelWriter.getWorkbook()));
        irrPerPhase.getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.000000000"));
        irrPerPhase.setCellValue(excelModel.getIrrPerPhase().doubleValue());
        currentRow = currentRow + 1;
        excelWriter.writeCellValue(4, currentRow, "IRR");
        Cell irrCell = excelWriter.getOrCreateCell(5, currentRow);
        irrCell.setCellStyle(MyStyleUtil.createDefaultCellStyle(excelWriter.getWorkbook()));
        irrCell.getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.000000000"));
        irrCell.setCellValue(excelModel.getIrr().doubleValue());
        // 强制公式进行计算
        excelWriter.getSheet().setForceFormulaRecalculation(true);
        // 写出到流
        excelWriter.flush(outputStream, true);
    }
}
