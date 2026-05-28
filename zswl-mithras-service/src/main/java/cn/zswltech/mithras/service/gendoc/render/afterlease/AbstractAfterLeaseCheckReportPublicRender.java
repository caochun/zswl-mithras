package cn.zswltech.mithras.service.gendoc.render.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.GovernmentSubjectItemType;
import cn.zswltech.mithras.service.enums.SubjectItemType;
import cn.zswltech.mithras.service.excel.MyStyleUtil;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportFinance;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportFinanceService;
import org.apache.poi.ss.usermodel.*;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/11/16
 * @description
 */
public abstract class AbstractAfterLeaseCheckReportPublicRender extends AbstractAfterLeaseCheckReportRender {
    protected static final int DEFAULT_COLUMN_SIZE = 25;

    @Resource
    protected AfterLeaseCheckReportFinanceService reportFinanceService;

    protected String getRenderFileName() {
        return "租后检查报告(现场/非现场)(公用事业类、民生消费类)" + GlobalConstants.OFFICE_EXCEL_SUFFIX;
    }

    protected ExcelWriter getExcelWriter() {
        InputStream inputStream = fileTemplateService.getTemplate(this.getTemplateVersion().getTemplateType(), this.getTemplateVersion().getTemplateFileName());
        ExcelWriter excelWriter = ExcelUtil.getReader(inputStream).getWriter();
        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setFont(Font.COLOR_NORMAL, (short) 11, "仿宋", false);
        return excelWriter;
    }

    protected void doFinanceReport(ExcelWriter excelWriter, NewAfterLeaseCheckPlanClient checkPlanClient) {
        // 查询相关财务报告
        List<NewAfterLeaseCheckReportFinance> reportFinanceList = reportFinanceService.listByCheckPlanClientId(checkPlanClient.getId());
        if (CollectionUtil.isEmpty(reportFinanceList)) {
            return;
        }
        // 处理财务报表
        Set<Long> clientIds = reportFinanceList.stream().map(NewAfterLeaseCheckReportFinance::getClientId).collect(Collectors.toSet());
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientIds);
        // 如果有保存财务报表则添加到其余sheet中
        List<NewAfterLeaseCheckReportFinance> lesseeFinanceList = new LinkedList<>();
        List<NewAfterLeaseCheckReportFinance> guarantorFinanceList = new LinkedList<>();
        for (NewAfterLeaseCheckReportFinance reportFinance : reportFinanceList) {
            if (Objects.equals(reportFinance.getClientProjectIdentity(), "LESSEE")) {
                lesseeFinanceList.add(reportFinance);
            } else if (Objects.equals(reportFinance.getClientProjectIdentity(), "GUARANTOR")) {
                guarantorFinanceList.add(reportFinance);
            }
        }
        // 处理承租人
        if (CollectionUtil.isNotEmpty(lesseeFinanceList)) {
            // 按客户id分组
            Map<Long, List<NewAfterLeaseCheckReportFinance>> lesseeFinanceMap = lesseeFinanceList.stream().collect(Collectors.groupingBy(NewAfterLeaseCheckReportFinance::getClientId));
            for (Map.Entry<Long, List<NewAfterLeaseCheckReportFinance>> entry : lesseeFinanceMap.entrySet()) {
                // 财务报表按照类型分组
                Map<String, NewAfterLeaseCheckReportFinance> reportFinanceMap = entry.getValue().stream().collect(Collectors.toMap(NewAfterLeaseCheckReportFinance::getSubjectType, e -> e));
                NewAfterLeaseCheckReportFinance cb = this.getCapitalBalance(reportFinanceMap);
                if (Objects.nonNull(cb)) {
                    String dataJson = cb.getDataJson();
                    if (StrUtil.isNotBlank(dataJson)) {
                        excelWriter.setSheet("承租人-" + clientNameMap.get(entry.getKey()) + "-" + SubjectItemType.CAPITAL_BALANCE.display());
                        List<CorpSubjectItemListRSP> dataList = JSONUtil.toList(dataJson, CorpSubjectItemListRSP.class);
                        this.generateFinanceExcel(excelWriter, dataList);
                    }
                }
                NewAfterLeaseCheckReportFinance p = reportFinanceMap.get(SubjectItemType.PROFIT.name());
                if (Objects.nonNull(p)) {
                    String dataJson = p.getDataJson();
                    if (StrUtil.isNotBlank(dataJson)) {
                        excelWriter.setSheet("承租人-" + clientNameMap.get(entry.getKey()) + "-" + SubjectItemType.PROFIT.display());
                        List<CorpSubjectItemListRSP> dataList = JSONUtil.toList(dataJson, CorpSubjectItemListRSP.class);
                        this.generateFinanceExcel(excelWriter, dataList);
                    }
                }
                NewAfterLeaseCheckReportFinance cf = reportFinanceMap.get(SubjectItemType.CASH_FLOW.name());
                if (Objects.nonNull(cf)) {
                    String dataJson = cf.getDataJson();
                    if (StrUtil.isNotBlank(dataJson)) {
                        excelWriter.setSheet("承租人-" + clientNameMap.get(entry.getKey()) + "-" + SubjectItemType.CASH_FLOW.display());
                        List<CorpSubjectItemListRSP> dataList = JSONUtil.toList(dataJson, CorpSubjectItemListRSP.class);
                        this.generateFinanceExcel(excelWriter, dataList);
                    }
                }
            }
        }
        // 处理担保人
        if (CollectionUtil.isNotEmpty(guarantorFinanceList)) {
            // 按客户id分组
            Map<Long, List<NewAfterLeaseCheckReportFinance>> guarantorFinanceMap = guarantorFinanceList.stream().collect(Collectors.groupingBy(NewAfterLeaseCheckReportFinance::getClientId));
            for (Map.Entry<Long, List<NewAfterLeaseCheckReportFinance>> entry : guarantorFinanceMap.entrySet()) {
                // 财务报表按照类型分组
                Map<String, NewAfterLeaseCheckReportFinance> reportFinanceMap = entry.getValue().stream().collect(Collectors.toMap(NewAfterLeaseCheckReportFinance::getSubjectType, e -> e));
                NewAfterLeaseCheckReportFinance cb = this.getCapitalBalance(reportFinanceMap);
                if (Objects.nonNull(cb)) {
                    String dataJson = cb.getDataJson();
                    if (StrUtil.isNotBlank(dataJson)) {
                        excelWriter.setSheet("担保人-" + clientNameMap.get(entry.getKey()) + "-" + SubjectItemType.CAPITAL_BALANCE.display());
                        List<CorpSubjectItemListRSP> dataList = JSONUtil.toList(dataJson, CorpSubjectItemListRSP.class);
                        this.generateFinanceExcel(excelWriter, dataList);
                    }
                }
                NewAfterLeaseCheckReportFinance p = reportFinanceMap.get(SubjectItemType.PROFIT.name());
                if (Objects.nonNull(p)) {
                    String dataJson = p.getDataJson();
                    if (StrUtil.isNotBlank(dataJson)) {
                        excelWriter.setSheet("担保人-" + clientNameMap.get(entry.getKey()) + "-" + SubjectItemType.PROFIT.display());
                        List<CorpSubjectItemListRSP> dataList = JSONUtil.toList(dataJson, CorpSubjectItemListRSP.class);
                        this.generateFinanceExcel(excelWriter, dataList);
                    }
                }
                NewAfterLeaseCheckReportFinance cf = reportFinanceMap.get(SubjectItemType.CASH_FLOW.name());
                if (Objects.nonNull(cf)) {
                    String dataJson = cf.getDataJson();
                    if (StrUtil.isNotBlank(dataJson)) {
                        excelWriter.setSheet("担保人-" + clientNameMap.get(entry.getKey()) + "-" + SubjectItemType.CASH_FLOW.display());
                        List<CorpSubjectItemListRSP> dataList = JSONUtil.toList(dataJson, CorpSubjectItemListRSP.class);
                        this.generateFinanceExcel(excelWriter, dataList);
                    }
                }
            }
        }
    }

    protected NewAfterLeaseCheckReportFinance getCapitalBalance(Map<String, NewAfterLeaseCheckReportFinance> map) {
        NewAfterLeaseCheckReportFinance cb = map.get(SubjectItemType.CAPITAL_BALANCE.name());
        NewAfterLeaseCheckReportFinance gcb = map.get(GovernmentSubjectItemType.GOV_CAPITAL_BALANCE.name());
        if (Objects.nonNull(cb)) {
            return cb;
        }
        return gcb;
    }

    protected void generateFinanceExcel(ExcelWriter excelWriter, List<CorpSubjectItemListRSP> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            return;
        }
        CellStyle tableHeaderStyle = MyStyleUtil.createHeadCellStyle(excelWriter.getWorkbook());
        CellStyle subjectHeaderStyle = this.createSubjectHeaderStyle(excelWriter.getWorkbook());
        CellStyle moneyStyle = MyStyleUtil.createMyMoneyCellStyle(excelWriter.getWorkbook());
        // 科目代码表头
        excelWriter.setColumnWidth(0, DEFAULT_COLUMN_SIZE);
        Cell subjectCodeHeaderCell = excelWriter.getOrCreateCell(0, 0);
        subjectCodeHeaderCell.setCellStyle(tableHeaderStyle);
        subjectCodeHeaderCell.setCellValue("科目代码");
        // 科目名称表头
        excelWriter.setColumnWidth(1, DEFAULT_COLUMN_SIZE);
        Cell subjectNameHeaderCell = excelWriter.getOrCreateCell(1, 0);
        subjectNameHeaderCell.setCellStyle(tableHeaderStyle);
        subjectNameHeaderCell.setCellValue("科目");
        // 填充数据
        for (int i = 0; i < dataList.size(); i++) {
            CorpSubjectItemListRSP rsp = dataList.get(i);
            int columnIndex = i + 2;
            // 报表年月表头
            excelWriter.setColumnWidth(columnIndex, DEFAULT_COLUMN_SIZE);
            Cell dateHeaderCell = excelWriter.getOrCreateCell(columnIndex, 0);
            dateHeaderCell.setCellStyle(tableHeaderStyle);
            dateHeaderCell.setCellValue(rsp.getYear() + "年" + rsp.getQuarter() + "月");
            // 报表具体数据
            int rowIndex = 1;
            for (CorpSubjectItemListRSP.SubjectItem subjectItem : rsp.getItemList()) {
                if (Objects.isNull(subjectItem.getSubjectCode())) {
                    continue;
                }
                if (subjectItem.getSubjectCode().contains("G00")) {
                    continue;
                }
                if (columnIndex == 2) {
                    // 科目信息各年份报表一致，只在第一次的时候填充
                    // 科目代码
                    Cell subjectCodeCell = excelWriter.getOrCreateCell(0, rowIndex);
                    subjectCodeCell.setCellStyle(subjectHeaderStyle);
                    subjectCodeCell.setCellValue(subjectItem.getSubjectCode());
                    // 科目名称
                    Cell subjectNameCell = excelWriter.getOrCreateCell(1, rowIndex);
                    subjectNameCell.setCellStyle(subjectHeaderStyle);
                    subjectNameCell.setCellValue(subjectItem.getSubjectName());
                }
                // 金额数据
                Cell moneyCell = excelWriter.getOrCreateCell(columnIndex, rowIndex);
                moneyCell.setCellStyle(moneyStyle);
                moneyCell.getCellStyle().setFont(MyStyleUtil.createFont(excelWriter.getWorkbook(), Font.COLOR_NORMAL, (short) 11, "宋体"));
                if (Objects.nonNull(subjectItem.getSubjectValue())) {
                    moneyCell.setCellValue(NumberUtil.div(subjectItem.getSubjectValue(), BigDecimal.valueOf(10000L)).doubleValue());
                } else {
                    moneyCell.setCellValue("");
                }
                rowIndex++;
            }
        }
    }

    protected void generateMainReport(ExcelWriter excelWriter, NewAfterLeaseCheckPlanClient checkPlanClient, AfterLeaseCheckReportBO reportBO) {
        // 填充报告基本信息
        excelWriter.writeCellValue("B2", reportBO.getClientName());
        excelWriter.writeCellValue("B3", reportBO.getBizDeptName());
        excelWriter.writeCellValue("D3", reportBO.getUserNameMap().get(checkPlanClient.getBelongSponsorId()));
        excelWriter.writeCellValue("B4", reportBO.getUserNameMap().get(checkPlanClient.getBelongSponsorId()));
        String start = LocalDateTimeUtil.format(reportBO.getReportBase().getCheckPeriodStart(), DatePattern.NORM_DATE_PATTERN);
        String end = LocalDateTimeUtil.format(reportBO.getReportBase().getCheckPeriodEnd(), DatePattern.NORM_DATE_PATTERN);
        excelWriter.writeCellValue("D4", start + "~" + end);
        excelWriter.writeCellValue("F4", checkPlanClient.getCheckTime());
        excelWriter.writeCellValue("B5", reportBO.getReportBase().getMainPerson());
        excelWriter.writeCellValue("D5", reportBO.getReportBase().getJob());
        excelWriter.writeCellValue("F5", reportBO.getReportBase().getContactWay());
        if (Objects.nonNull(reportBO.getReportBase().getContractAmount())) {
            BigDecimal b = NumberUtil.div(reportBO.getReportBase().getContractAmount().toString(), GlobalConstants.MONEY_MULTIPLE).divide(BigDecimal.valueOf(10000), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
            excelWriter.writeCellValue("B6",  b.toPlainString() + "万元");
        }
        if (Objects.nonNull(reportBO.getReportBase().getRiskExposure())) {
            BigDecimal b = NumberUtil.div(reportBO.getReportBase().getRiskExposure().toString(), GlobalConstants.MONEY_MULTIPLE).divide(BigDecimal.valueOf(10000), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
            excelWriter.writeCellValue("D6",  b.toPlainString() + "万元");
        }
        excelWriter.writeCellValue("F6", LocalDateTimeUtil.format(reportBO.getReportBase().getDeadline(),DatePattern.NORM_DATE_PATTERN));
        // 填充报告内容
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> fieldDataMap = reportBO.getFieldDataList().stream().filter(e -> Objects.equals(e.getModuleIndex(), 0)).collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e));
        // 当地区域经济情况
        excelWriter.writeCellValue("F8", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_1_01")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F9", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_1_02")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        // 承租人经营情况
        excelWriter.writeCellValue("F10", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_2_01")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F11", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_2_02")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F12", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_2_03")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F13", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_2_04")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F14", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_2_05")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        // 担保人经营情况
        excelWriter.writeCellValue("F15", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_3_01")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F16", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_3_02")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F17", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_3_03")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F18", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_3_04")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F19", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_3_05")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        // 租赁物
        excelWriter.writeCellValue("F20", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_01")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F21", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_02")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F22", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_03")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F23", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_04")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F24", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_05")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F25", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_06")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F26", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_07")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F27", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_08")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F28", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_09")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        excelWriter.writeCellValue("F29", this.transform(Optional.ofNullable(fieldDataMap.get("P_C_4_10")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null)));
        // 填充报告总结
        excelWriter.writeCellValue("B30", Optional.ofNullable(fieldDataMap.get("P_S_1_01")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null));
        excelWriter.writeCellValue("B31", Optional.ofNullable(fieldDataMap.get("P_S_1_02")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null));
        excelWriter.writeCellValue("B32", Optional.ofNullable(fieldDataMap.get("P_S_1_03")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null));
    }

    protected String transform(String checkResult) {
        if (checkResult == null) {
            return null;
        }
        if (Objects.equals(checkResult, "1")) {
            return "是";
        } else if (Objects.equals(checkResult, "0")) {
            return "否";
        } else {
            return "不适用";
        }
    }

    protected CellStyle createSubjectHeaderStyle(Workbook workbook) {
        CellStyle cellStyle = MyStyleUtil.createDefaultCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        font.setColor(Font.COLOR_NORMAL);
        cellStyle.setFont(font);
        return cellStyle;
    }
}
