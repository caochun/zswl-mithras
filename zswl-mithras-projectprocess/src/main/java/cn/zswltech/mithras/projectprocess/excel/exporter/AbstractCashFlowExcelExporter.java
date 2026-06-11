package cn.zswltech.mithras.projectprocess.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentActualDetailData;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentBaseInfoData;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentCashFlowQueryPort;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/28
 * @description
 */
public abstract class AbstractCashFlowExcelExporter<T> {
    @Resource
    protected PaymentCashFlowQueryPort paymentCashFlowQueryPort;

    protected abstract CashFlowRichExcelModel prepare(T t);

    public void export(OutputStream outputStream, T t) {
        CashFlowRichExcelModel model = this.prepare(t);
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 默认列宽
        excelWriter.setColumnWidth(-1, 20);
        // 第1行
        int rowIndex = 0;
        Cell A1 = excelWriter.getOrCreateCell(0, rowIndex);
        A1.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        A1.setCellValue("合同费率明细");
        Cell B1 = excelWriter.getOrCreateCell(1, rowIndex);
        B1.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B1.setCellValue("申报授信金额（元）");
        Cell C1 = excelWriter.getOrCreateCell(2, rowIndex);
        C1.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
        if (Objects.nonNull(model.getProjectAmount())) {
            C1.setCellValue(model.getProjectAmount().doubleValue());
        }
        Cell D1 = excelWriter.getOrCreateCell(3, rowIndex);
        D1.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D1.setCellValue("现金流类别");
        Cell E1 = excelWriter.getOrCreateCell(4, rowIndex);
        E1.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        E1.setCellValue("费率");
        Cell F1 = excelWriter.getOrCreateCell(5, rowIndex);
        F1.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        F1.setCellValue("金额");
        // 第2行
        rowIndex++;
        Cell B2 = excelWriter.getOrCreateCell(1, rowIndex);
        B2.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B2.setCellValue("租赁期限（月）");
        Cell C2 = excelWriter.getOrCreateCell(2, rowIndex);
        C2.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        if (Objects.nonNull(model.getMonthCount())) {
            C2.setCellValue(model.getMonthCount());
        }
        Cell D2 = excelWriter.getOrCreateCell(3, rowIndex);
        D2.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D2.setCellValue("服务费");
        Cell E2 = excelWriter.getOrCreateCell(4, rowIndex);
        E2.setCellStyle(this.getPercentCellStyle(excelWriter.getWorkbook()));
        E2.setCellFormula("F2/C1");
        Cell F2 = excelWriter.getOrCreateCell(5, rowIndex);
        F2.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
        if (Objects.nonNull(model.getConsultingFee())) {
            F2.setCellValue(model.getConsultingFee().doubleValue());
        }
        // 第3行
        rowIndex++;
        Cell B3 = excelWriter.getOrCreateCell(1, rowIndex);
        B3.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B3.setCellValue("年还款次数");
        Cell C3 = excelWriter.getOrCreateCell(2, rowIndex);
        C3.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        if (Objects.nonNull(model.getRepayTimesInYear())) {
            C3.setCellValue(model.getRepayTimesInYear());
        }
        Cell D3 = excelWriter.getOrCreateCell(3, rowIndex);
        D3.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D3.setCellValue("保证金");
        Cell E3 = excelWriter.getOrCreateCell(4, rowIndex);
        E3.setCellStyle(this.getPercentCellStyle(excelWriter.getWorkbook()));
        E3.setCellFormula("F3/C1");
        Cell F3 = excelWriter.getOrCreateCell(5, rowIndex);
        F3.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
        if (Objects.nonNull(model.getEarnest())) {
            F3.setCellValue(model.getEarnest().doubleValue());
        }
        // 第4行
        rowIndex++;
        Cell B4 = excelWriter.getOrCreateCell(1, rowIndex);
        B4.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B4.setCellValue("年利率");
        Cell C4 = excelWriter.getOrCreateCell(2, rowIndex);
        C4.setCellStyle(this.getPercentCellStyle(excelWriter.getWorkbook()));
        Cell D4 = excelWriter.getOrCreateCell(3, rowIndex);
        D4.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D4.setCellValue("首期租金");
        Cell E4 = excelWriter.getOrCreateCell(4, rowIndex);
        E4.setCellStyle(this.getPercentCellStyle(excelWriter.getWorkbook()));
        E4.setCellFormula("F4/C1");
        Cell F4 = excelWriter.getOrCreateCell(5, rowIndex);
        F4.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
        C4.setCellValue(model.getInterestRate().doubleValue());
        if (Objects.nonNull(model.getDownPayment())) {
            F4.setCellValue(model.getDownPayment().doubleValue());
        } else {
            F4.setCellValue(0);
        }
        // 第5行
        rowIndex++;
        Cell B5 = excelWriter.getOrCreateCell(1, rowIndex);
        B5.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B5.setCellValue("支付方式");
        Cell C5 = excelWriter.getOrCreateCell(2, rowIndex);
        C5.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        if (Objects.nonNull(model.getPayWay())) {
            C5.setCellValue(model.getPayWay());
        }
        Cell D5 = excelWriter.getOrCreateCell(3, rowIndex);
        D5.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D5.setCellValue("留购款");
        Cell E5 = excelWriter.getOrCreateCell(4, rowIndex);
        E5.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        Cell F5 = excelWriter.getOrCreateCell(5, rowIndex);
        F5.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
        if (Objects.nonNull(model.getNominalPrice())) {
            F5.setCellValue(model.getNominalPrice().doubleValue());
        }
        // 第6行
        rowIndex++;
        Cell B6 = excelWriter.getOrCreateCell(1, rowIndex);
        B6.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B6.setCellValue("租金计算方式");
        Cell C6 = excelWriter.getOrCreateCell(2, rowIndex);
        C6.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        C6.setCellValue(model.getRentCalculateWay());
        Cell D6 = excelWriter.getOrCreateCell(3, rowIndex);
        D6.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D6.setCellValue("先付/后付");
        Cell E6 = excelWriter.getOrCreateCell(4, rowIndex);
        E6.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        E6.setCellFormula("IF(C5=\"后付\",0,1)");
        Cell F6 = excelWriter.getOrCreateCell(5, rowIndex);
        F6.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        // 第7行
        rowIndex++;
        Cell B7 = excelWriter.getOrCreateCell(1, rowIndex);
        B7.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B7.setCellValue("还款期数");
        Cell C7 = excelWriter.getOrCreateCell(2, rowIndex);
        C7.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        if (Objects.nonNull(model.getRepayTimesTotal())) {
            C7.setCellValue(model.getRepayTimesTotal());
        }
        Cell D7 = excelWriter.getOrCreateCell(3, rowIndex);
        D7.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D7.setCellValue("计划起租日");
        Cell E7 = excelWriter.getOrCreateCell(4, rowIndex);
        E7.setCellStyle(this.getDateCellStyle(excelWriter.getWorkbook()));
        if (Objects.nonNull(model.getStartRentDate())) {
            E7.setCellValue(model.getStartRentDate());
        }
        Cell F7 = excelWriter.getOrCreateCell(5, rowIndex);
        F7.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        // 第8行
        rowIndex++;
        Cell B8 = excelWriter.getOrCreateCell(1, rowIndex);
        B8.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B8.setCellValue("期利率");
        Cell C8 = excelWriter.getOrCreateCell(2, rowIndex);
        C8.setCellStyle(this.getPercentCellStyle(excelWriter.getWorkbook()));
        C8.setCellFormula("C4/C3");
        Cell D8 = excelWriter.getOrCreateCell(3, rowIndex);
        D8.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        Cell E8 = excelWriter.getOrCreateCell(4, rowIndex);
        E8.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        Cell F8 = excelWriter.getOrCreateCell(5, rowIndex);
        F8.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        // 第9行
        rowIndex++;
        Cell B9 = excelWriter.getOrCreateCell(1, rowIndex);
        B9.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B9.setCellValue("PMT");
        Cell C9 = excelWriter.getOrCreateCell(2, rowIndex);
        C9.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        C9.setCellFormula("ROUND(PMT(C8,C7,-(C1-F4),,E6),2)");
        Cell D9 = excelWriter.getOrCreateCell(3, rowIndex);
        D9.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D9.setCellValue("IRR");
        Cell E9 = excelWriter.getOrCreateCell(4, rowIndex);
        E9.setCellStyle(this.getPercentCellStyle(excelWriter.getWorkbook()));
        if (Objects.nonNull(model.getIrr())) {
            E9.setCellValue(model.getIrr().doubleValue());
        }
        Cell F9 = excelWriter.getOrCreateCell(5, rowIndex);
        F9.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
        // 第10行
        rowIndex++;
        Cell A10 = excelWriter.getOrCreateCell(0, rowIndex);
        A10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        A10.setCellValue("租金偿还方案");
        Cell B10 = excelWriter.getOrCreateCell(1, rowIndex);
        B10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        B10.setCellValue("日期");
        Cell C10 = excelWriter.getOrCreateCell(2, rowIndex);
        C10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        C10.setCellValue("期项");
        Cell D10 = excelWriter.getOrCreateCell(3, rowIndex);
        D10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        D10.setCellValue("现金流金额（元）");
        Cell E10 = excelWriter.getOrCreateCell(4, rowIndex);
        E10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        if (StrUtil.isBlank(model.getMoneyCellName())) {
            E10.setCellValue("租金（元）");
        } else {
            E10.setCellValue(model.getMoneyCellName());
        }
        Cell F10 = excelWriter.getOrCreateCell(5, rowIndex);
        F10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        F10.setCellValue("本金（元）");
        Cell G10 = excelWriter.getOrCreateCell(6, rowIndex);
        G10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        G10.setCellValue("利息（元）");
        Cell H10 = excelWriter.getOrCreateCell(7, rowIndex);
        H10.setCellStyle(this.getHeaderCellStyle(excelWriter.getWorkbook()));
        H10.setCellValue("剩余本金（元）");
//        // 第11行（第0期）
//        rowIndex++;
//        Cell B11 = excelWriter.getOrCreateCell(1, rowIndex);
//        B11.setCellStyle(this.getDateCellStyle(excelWriter.getWorkbook()));
//        B11.setCellValue(model.getPhaseZeroDate());
//        Cell C11 = excelWriter.getOrCreateCell(2, rowIndex);
//        C11.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
//        C11.setCellValue(0);
//        Cell D11 = excelWriter.getOrCreateCell(3, rowIndex);
//        D11.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
//        D11.setCellFormula("-C1+F2+F3+F4");
//        Cell E11 = excelWriter.getOrCreateCell(4, rowIndex);
//        E11.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
//        Cell F11 = excelWriter.getOrCreateCell(5, rowIndex);
//        F11.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
//        Cell G11 = excelWriter.getOrCreateCell(6, rowIndex);
//        G11.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
//        Cell H11 = excelWriter.getOrCreateCell(7, rowIndex);
//        H11.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
        // 遍历现金流表填充数据
        if (CollectionUtil.isNotEmpty(model.getCashFlowList())) {
            for (CashFlowExcelModel cashFlowExcelModel : model.getCashFlowList()) {
                rowIndex++;
                Cell dataCell = excelWriter.getOrCreateCell(1, rowIndex);
                dataCell.setCellStyle(this.getDateCellStyle(excelWriter.getWorkbook()));
                if (Objects.nonNull(cashFlowExcelModel.getCashFlowDate())) {
                    dataCell.setCellValue(cashFlowExcelModel.getCashFlowDate());
                }
                Cell phaseCell = excelWriter.getOrCreateCell(2, rowIndex);
                phaseCell.setCellStyle(this.getDefaultCellStyle(excelWriter.getWorkbook(), false));
                if (Objects.nonNull(cashFlowExcelModel.getCashFlowPhase())) {
                    phaseCell.setCellValue(cashFlowExcelModel.getCashFlowPhase());
                }
                Cell cashFlowCell = excelWriter.getOrCreateCell(3, rowIndex);
                cashFlowCell.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
                if (Objects.nonNull(cashFlowExcelModel.getCashFlowAmount())) {
                    cashFlowCell.setCellValue(cashFlowExcelModel.getCashFlowAmount().doubleValue());
                }
                Cell rentCell = excelWriter.getOrCreateCell(4, rowIndex);
                rentCell.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
                if (Objects.nonNull(cashFlowExcelModel.getRent())) {
                    rentCell.setCellValue(cashFlowExcelModel.getRent().doubleValue());
                }
                Cell principalCell = excelWriter.getOrCreateCell(5, rowIndex);
                principalCell.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
                if (Objects.nonNull(cashFlowExcelModel.getPrincipal())) {
                    principalCell.setCellValue(cashFlowExcelModel.getPrincipal().doubleValue());
                }
                Cell interestCell = excelWriter.getOrCreateCell(6, rowIndex);
                interestCell.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
                if (Objects.nonNull(cashFlowExcelModel.getInterest())) {
                    interestCell.setCellValue(cashFlowExcelModel.getInterest().doubleValue());
                }
                Cell remainingPrincipalCell = excelWriter.getOrCreateCell(7, rowIndex);
                remainingPrincipalCell.setCellStyle(this.getMoneyCellStyle(excelWriter.getWorkbook()));
                if (Objects.nonNull(cashFlowExcelModel.getRemainingPrincipal())) {
                    remainingPrincipalCell.setCellValue(cashFlowExcelModel.getRemainingPrincipal().doubleValue());
                }
            }
            // 强制进行公式计算
            excelWriter.getSheet().setForceFormulaRecalculation(true);
            // 写出到输入流
            excelWriter.flush(outputStream, true);
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle cellStyle = this.getDefaultCellStyle(workbook, true);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    private CellStyle getDateCellStyle(Workbook workbook) {
        CellStyle cellStyle = this.getDefaultCellStyle(workbook, false);
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
        return cellStyle;
    }

    private CellStyle getPercentCellStyle(Workbook workbook) {
        CellStyle cellStyle = this.getDefaultCellStyle(workbook, false);
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        return cellStyle;
    }

    private CellStyle getMoneyCellStyle(Workbook workbook) {
        CellStyle cellStyle = this.getDefaultCellStyle(workbook, false);
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        return cellStyle;
    }

    private CellStyle getDefaultCellStyle(Workbook workbook, boolean fontBold) {
        // 字体
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setFontHeightInPoints((short) 10);
        font.setBold(fontBold);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        // 水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 超出自动换行
        cellStyle.setWrapText(true);
        // 设置边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        return cellStyle;
    }

    protected CashFlowExcelModel getZeroPhaseCashFlow(CashFlowRichExcelModel cashFlowRichExcelModel, Long receiptId) {
        CashFlowExcelModel zeroPhaseCashFlow = new CashFlowExcelModel();
        zeroPhaseCashFlow.setCashFlowPhase(0);
        // 第0期现金流金额 = (-1 * 项目金额) + 保证金 + 服务费 + 首期租金 + 手续费
        BigDecimal amount = cashFlowRichExcelModel.getProjectAmount()
                .multiply(BigDecimal.valueOf(-1))
                .add(Optional.ofNullable(cashFlowRichExcelModel.getEarnest()).orElse(BigDecimal.ZERO))
                .add(Optional.ofNullable(cashFlowRichExcelModel.getConsultingFee()).orElse(BigDecimal.ZERO))
                .add(Optional.ofNullable(cashFlowRichExcelModel.getCommission()).orElse(BigDecimal.ZERO))
                .add(Optional.ofNullable(cashFlowRichExcelModel.getDownPayment()).orElse(BigDecimal.ZERO));
        zeroPhaseCashFlow.setCashFlowAmount(amount);
        if (Objects.isNull(receiptId)) {
            zeroPhaseCashFlow.setCashFlowDate(cashFlowRichExcelModel.getStartRentDate());
        } else {
            List<PaymentBaseInfoData> paymentBaseInfoList = paymentCashFlowQueryPort.listPaymentBaseInfoByReceiptId(receiptId);
            // 确定第0期的日期
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                // 没有关联付款取计划起租日
                zeroPhaseCashFlow.setCashFlowDate(cashFlowRichExcelModel.getStartRentDate());
            } else {
                PaymentBaseInfoData paymentBaseInfo = paymentBaseInfoList.get(0);
                // 查询付款核销记录
                List<PaymentActualDetailData> paymentActualDetailList = paymentCashFlowQueryPort.listActualDetailByPaymentId(paymentBaseInfo.getId());
                if (CollectionUtil.isEmpty(paymentActualDetailList)) {
                    // 取计划起租日
                    zeroPhaseCashFlow.setCashFlowDate(cashFlowRichExcelModel.getStartRentDate());
                } else {
                    List<PaymentActualDetailData> filterList = paymentActualDetailList.stream()
                            .filter(item -> Objects.nonNull(item.getPaidInDate()))
                            .sorted(Comparator.comparing(PaymentActualDetailData::getPaidInDate))
                            .collect(Collectors.toList());
                    PaymentActualDetailData paymentActualDetail = filterList.get(0);
                    if (Objects.nonNull(paymentActualDetail)) {
                        zeroPhaseCashFlow.setCashFlowDate(paymentActualDetail.getPaidInDate());
                    } else {
                        // 取计划起租日
                        zeroPhaseCashFlow.setCashFlowDate(cashFlowRichExcelModel.getStartRentDate());
                    }
                }
            }
        }
        return zeroPhaseCashFlow;
    }

    protected void fixLastCashFlow(CashFlowRichExcelModel cashFlowRichExcelModel) {
        if (CollectionUtil.isNotEmpty(cashFlowRichExcelModel.getCashFlowList())) {
            CashFlowExcelModel last = cashFlowRichExcelModel.getCashFlowList().get(cashFlowRichExcelModel.getCashFlowList().size() - 1);
            BigDecimal earnest = cashFlowRichExcelModel.getEarnest() == null ? BigDecimal.ZERO : cashFlowRichExcelModel.getEarnest();
            BigDecimal nominalPrice = cashFlowRichExcelModel.getNominalPrice() == null ? BigDecimal.ZERO : cashFlowRichExcelModel.getNominalPrice();
            BigDecimal b = last.getCashFlowAmount().subtract(earnest).add(nominalPrice);
            last.setCashFlowAmount(b);
        }
    }
}
