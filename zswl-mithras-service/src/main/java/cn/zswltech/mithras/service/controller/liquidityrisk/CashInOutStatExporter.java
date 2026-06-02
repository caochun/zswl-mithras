package cn.zswltech.mithras.service.controller.liquidityrisk;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.dto.liquidityrisk.CashInOutStatRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author luyi
 */
@Slf4j
@Component
public class CashInOutStatExporter {

    @SneakyThrows
    public void export(HttpServletResponse response, List<CashInOutStatRSP> result, String filename) {
        //--write to excel
        String downloadFileName = URLEncoder.encode(filename, "UTF-8");
        try (
                ExcelWriter writer = writeToExcel(result)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            writer.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
            outputStream.write(bos.toByteArray());
        }

    }


    private static ExcelWriter writeToExcel(List<CashInOutStatRSP> result) {
        result.sort(Comparator.comparing(m -> Optional.ofNullable(m.getFinancialChannel()).map(outStat -> outStat.get(0)).orElse(null)));
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //写头部
        writer.merge(0, 0, 0, 6, "资金端", true);
        writer.merge(0, 1, 7, 7, "流动性盈缺", true);
        writer.merge(0, 1, 8, 8, "期限错配", true);
        writer.merge(0, 0, 9, 15, "资产端", true);
        writer.writeCellValue(0, 1, "融资渠道");
        writer.writeCellValue(1, 1, "融资编码");
        writer.writeCellValue(2, 1, "融资金额（万元）");
        writer.writeCellValue(3, 1, "现金流出时间");
        writer.writeCellValue(4, 1, "现金流出金额（万元）");
        writer.writeCellValue(5, 1, "现金流出本金（万元）");
        writer.writeCellValue(6, 1, "现金流出利息（万元）");
        writer.writeCellValue(9, 1, "项目名称");
        writer.writeCellValue(10, 1, "合同编号");
        writer.writeCellValue(11, 1, "合同总金额（万元）");
        writer.writeCellValue(12, 1, "现金流入时间");
        writer.writeCellValue(13, 1, "现金流入金额（万元）");
        writer.writeCellValue(14, 1, "现金流入本金（万元）");
        writer.writeCellValue(15, 1, "现金流入利息（万元）");
        //日期格式
        DataFormat dataFormat = writer.getWorkbook().createDataFormat();
        CellStyle cellStyle = writer.getStyleSet().getCellStyleForDate();
        short format = dataFormat.getFormat("yyyy-MM-dd");
        cellStyle.setDataFormat(format);
        //写行数据
        writer.setCurrentRow(2);
        Set<String> already = new HashSet<>();
        for (CashInOutStatRSP record : result) {
            boolean codeExists = already.contains(record.getFinancialCode());
            already.add(record.getFinancialCode());
            int startRow = writer.getCurrentRow();
            List<CashInOutStatRSP.InRecord> assetsList = record.getInRecordList();
            if (assetsList.isEmpty()) {
                writer.writeRow(ListUtil.of(
                        record.getFinancialChannel(),
                        record.getFinancialCode(),
                        toWanYuan(record.getFinancialAmount()), record.getCashOutDate(),
                        toWanYuan(record.getCashOutTotal()), toWanYuan(record.getCashOutPrincipal()), toWanYuan(record.getCashOutInterest())
                        , "", "",
                        null, null, null, null, null, null, null
                ));
            }
            for (CashInOutStatRSP.InRecord inRecord : assetsList) {
                writer.writeRow(ListUtil.of(
                        record.getFinancialChannel(), record.getFinancialCode(), toWanYuan(record.getFinancialAmount()),
                        record.getCashOutDate(),
                        toWanYuan(record.getCashOutTotal()), toWanYuan(record.getCashOutPrincipal()), toWanYuan(record.getCashOutInterest())
                        , "", "",
                        inRecord.getProjName(), inRecord.getContractCode(), toWanYuan(inRecord.getContractAmount()), inRecord.getCashInDate(),
                        toWanYuan(inRecord.getCashInTotal()), toWanYuan(inRecord.getCashInPrincipal()), toWanYuan(inRecord.getCashInInterest())
                ));
                //着色
                if (codeExists) {
                    for (int i = 9; i <= 15; i++) {
                        CellStyle style = writer.createCellStyle(i, writer.getCurrentRow() - 1);
                        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        writer.setStyle(style, i, startRow);
                        if (i == 12) {
                            style.setDataFormat(format);
                        }
                        if (i == 9) {
                            addCellComment(writer.getCell(i, startRow), "该资产已有其他现金流关联", true);
                        }
                    }
                }
            }


            //合并行
            int endRow = writer.getCurrentRow() - 1;
            if (assetsList.size() > 1) {
                writer.merge(startRow, endRow, 0, 0, record.getFinancialChannel(), false);
                writer.merge(startRow, endRow, 1, 1, record.getFinancialCode(), false);
                writer.merge(startRow, endRow, 2, 2, toWanYuan(record.getFinancialAmount()), false);
                writer.merge(startRow, endRow, 3, 3, record.getCashOutDate(), false);
                writer.merge(startRow, endRow, 4, 4, toWanYuan(record.getCashOutTotal()), false);
                writer.merge(startRow, endRow, 5, 5, toWanYuan(record.getCashOutPrincipal()), false);
                writer.merge(startRow, endRow, 6, 6, toWanYuan(record.getCashOutInterest()), false);

            }
            //金额不足
            long inTotal = assetsList.stream().mapToLong(CashInOutStatRSP.InRecord::getCashInTotal).sum();
            String text = inTotal < record.getCashOutTotal() ? "Y" : "";
            writer.writeCellValue(7, startRow, text);
            if (StrUtil.isNotBlank(text)) {
                CellStyle style = writer.createCellStyle(7, startRow);
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                writer.setStyle(style, 7, startRow);
            }
            if (assetsList.size() > 1) {
                writer.merge(startRow, endRow, 7, 7, text, false);
            }
            //日期错配
            long beforeInTotal = assetsList.stream().filter(e -> !e.getCashInDate().isAfter(record.getCashOutDate()))
                    .mapToLong(CashInOutStatRSP.InRecord::getCashInTotal).sum();
            text = /*总资金足够*/inTotal >= record.getCashOutTotal()
                    &&
                    /*日期过滤后不足*/beforeInTotal < record.getCashOutTotal() ? "Y" : "";
            writer.writeCellValue(8, startRow, text);
            if (StrUtil.isNotBlank(text)) {
                CellStyle style = writer.createCellStyle(8, startRow);
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                writer.setStyle(style, 8, startRow);
            }
            if (assetsList.size() > 1) {
                writer.merge(startRow, endRow, 8, 8, text, false);
            }
        }
        //设置日期格式
        writer.setColumnStyle(5, cellStyle);
        return writer;
    }

    /**
     * 通过id获取合同信息
     */
    private ContractBaseInfo getContractBaseInfo(Long id) {
        if (contractBaseInfoMap.containsKey(id)) {
            return contractBaseInfoMap.get(id);
        }
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getById(id);
        contractBaseInfoMap.put(id, baseInfo);
        return contractBaseInfoMap.get(id);
    }

    /**
     * 通过还本付息id获取信息
     */
    private FundReceiptRepayBaseInfo getFundReceiptInfo(Long id) {
        if (fundReceiptRepayBaseInfoMap.containsKey(id)) {
            return fundReceiptRepayBaseInfoMap.get(id);
        }
        FundReceiptRepayBaseInfo baseInfo = getBean(FundReceiptRepayBaseInfoService.class).getById(id);
        fundReceiptRepayBaseInfoMap.put(id, baseInfo);
        return fundReceiptRepayBaseInfoMap.get(id);
    }

    /**
     * 根据融资id获取融资信息
     */

    private FundFinancingBaseInfo getFundFinancingInfo(Long id) {
        if (fundFinnancingRepayBaseInfoMap.containsKey(id)) {
            return fundFinnancingRepayBaseInfoMap.get(id);
        }
        FundFinancingBaseInfo baseInfo = getBean(FundFinancingBaseInfoService.class).getById(id);
        fundFinnancingRepayBaseInfoMap.put(id, baseInfo);
        return fundFinnancingRepayBaseInfoMap.get(id);

    }


    public static void addCellComment(Cell cell, String value, boolean isXlsx) {
        Sheet sheet = cell.getSheet();
        cell.removeCellComment();
        Drawing drawing = sheet.createDrawingPatriarch();
        Comment comment;
        if (isXlsx) {
            // 创建批注
            comment = drawing.createCellComment(new XSSFClientAnchor(1, 1, 1, 1, 1, 1, 1, 1));
            // 输入批注信息
            comment.setString(new XSSFRichTextString(value));
            // 将批注添加到单元格对象中
        } else {
            // 创建批注
            comment = drawing.createCellComment(new HSSFClientAnchor(1, 1, 1, 1, (short) 1, 1, (short) 1, 1));
            // 输入批注信息
            comment.setString(new HSSFRichTextString(value));
            // 将批注添加到单元格对象中
        }
        cell.setCellComment(comment);
    }

    public static String toWanYuan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return bigDecimal.divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString();
    }

    Map<Long, ContractBaseInfo> contractBaseInfoMap = new HashMap<>();
    Map<Long, FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfoMap = new HashMap<>();
    Map<Long, FundFinancingBaseInfo> fundFinnancingRepayBaseInfoMap = new HashMap<>();

}
