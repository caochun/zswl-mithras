package cn.zswltech.mithras.budget.application.excel;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class BudgetCashFlowExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "日期", headerOrder = 10, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate cashFlowDate;

    @SimpleExcelHeader(headerName = "期项", headerOrder = 20)
    private Integer cashFlowPhase;

    @SimpleExcelHeader(headerName = "现金流金额（元）", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal cashFlowAmount;

    @SimpleExcelHeader(headerName = "租金（元）", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal rent;

    @SimpleExcelHeader(headerName = "本金（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal principal;

    @SimpleExcelHeader(headerName = "利息（元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal interest;

    @SimpleExcelHeader(headerName = "剩余本金（元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal remainingPrincipal;
}
