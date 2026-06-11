package cn.zswltech.mithras.capital.application.excel;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 资金端-付款导出使用
 * @author: huangping
 * @date: 2025/12/5  9:44
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessFlowFundPaymentModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "核销状态", headerOrder = 10)
    private String writeOffStatusDisplay;


    @SimpleExcelHeader(headerName = "融资机构", headerOrder = 20)
    private String financingRoute;


    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 30)
    private String financingCode;


    @SimpleExcelHeader(headerName = "期项", headerOrder = 40)
    private Integer phase;

    @SimpleExcelHeader(headerName = "现⾦流项⽬", headerOrder = 50)
    private String cashFlowItemDisplay;

    @SimpleExcelHeader(headerName = "应付日期", headerOrder = 60)
    private String date;

    @SimpleExcelHeader(headerName = "应付金额（元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal amount;

    @SimpleExcelHeader(headerName = "应付本金（元）", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal principalAmount;

    @SimpleExcelHeader(headerName = "应付利息（元）", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal interestAmount;


    @SimpleExcelHeader(headerName = "已付金额（元）", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal actualVerifyAmount;


    @SimpleExcelHeader(headerName = "已付本金（元）", headerOrder = 110, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal actualVerifyPrincipalAmount;


    @SimpleExcelHeader(headerName = "已付利息（元）", headerOrder = 120, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal actualVerifyInterestAmount;


    @SimpleExcelHeader(headerName = "最近付款日", headerOrder = 130, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate cashFlowDate;


}
