package cn.zswltech.mithras.service.excel.model.monthly;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class MonthlyRPExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "借据编号", headerOrder = 3)
    private String receiptCode;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 1)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 2)
    private String contractCode;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 0)
    private String clientName;

    @SimpleExcelHeader(headerName = "租赁类型", headerOrder = 8)
    private String leaseType;

    @SimpleExcelHeader(headerName = "税率/%", headerOrder = 9)
    private BigDecimal taxRateExcel;

    @SimpleExcelHeader(headerName = "本月收入金额（含税）/元", headerOrder = 7)
    private BigDecimal incomeSumExcel;

    @SimpleExcelHeader(headerName = "本月收入金额（不含税）/元", headerOrder = 6)
    private BigDecimal incomeWithoutTaxSumExcel;

    @SimpleExcelHeader(headerName = "当前是否逾期", headerOrder = 10)
    private String overdueType;

    @SimpleExcelHeader(headerName = "月份", headerOrder = 5)
    private String yearAndMonth;

    @SimpleExcelHeader(headerName = "实际起租日", headerOrder = 4)
    private LocalDate actualLeaseDate;

    @SimpleExcelHeader(headerName = "最近一期全额偿还租金期次", headerOrder = 11)
    private Integer theLatestFullRefundRentPeriod;

    @SimpleExcelHeader(headerName = "最近一期全额偿还租金应收日期", headerOrder = 12)
    private LocalDate theLatestFullRefundRentDate;

    @SimpleExcelHeader(headerName = "最近一期全额偿还租金应收剩余本金/元", headerOrder = 13)
    private BigDecimal theLatestFullRefundRentCapitalExcel;

    @SimpleExcelHeader(headerName = "合同名义利率/%", headerOrder = 14)
    private BigDecimal contractNominalInterestRateExcel;
    
}
