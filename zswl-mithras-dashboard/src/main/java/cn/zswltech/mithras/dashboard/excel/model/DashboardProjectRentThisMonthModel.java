package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectRentThisMonthModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "投放金额(元)", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private String creditAmount;

    @SimpleExcelHeader(headerName = "剩余租金金额（元）", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String rent;

    @SimpleExcelHeader(headerName = "本期期项", headerOrder = 50)
    private Integer phase;

    @SimpleExcelHeader(headerName = "本期应收日期", headerOrder = 60, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate planCollectionDate;

    @SimpleExcelHeader(headerName = "实际还款日期", headerOrder = 70, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate actualCollectionDate;

    @SimpleExcelHeader(headerName = "是否逾期", headerOrder = 80)
    private String isOverdue;

    @SimpleExcelHeader(headerName = "本期本金", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String principalAmount;

    @SimpleExcelHeader(headerName = "本期利息", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String interestAmount;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 110)
    private String clientName;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 120)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "业务模式", headerOrder = 130)
    private String leaseTypeDisplay;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 140)
    private String lesseeNames;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 150)
    private String guarantorNames;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 160)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 170)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 180)
    private String projCosponsorUserNames;
}