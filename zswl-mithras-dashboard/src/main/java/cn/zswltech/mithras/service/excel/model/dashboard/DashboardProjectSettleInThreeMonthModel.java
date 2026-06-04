package cn.zswltech.mithras.service.excel.model.dashboard;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectSettleInThreeMonthModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "到期日", headerOrder = 30, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate deadline;

    @SimpleExcelHeader(headerName = "剩余期限(月)", headerOrder = 40)
    private String remainingDuration;

    @SimpleExcelHeader(headerName = "已收租金金额（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String collectionAmount;

    @SimpleExcelHeader(headerName = "剩余租金金额（元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String remainingAmount;

    @SimpleExcelHeader(headerName = "保证金金额（元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String earnestBalanceAmount;

    @SimpleExcelHeader(headerName = "最后一期租金计划还款日", headerOrder = 80, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate lastRentPlanCollectionDate;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 90)
    private String clientName;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 100)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 110)
    private String lesseeNames;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 120)
    private String guarantorNames;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 130)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 140)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 150)
    private String projCosponsorUserNames;

    @SimpleExcelHeader(headerName = "已收本金（元）", headerOrder = 160, columnStyle = ColumnStyleEnum.MONEY)
    private String collectionPrincipalAmount;

    @SimpleExcelHeader(headerName = "已收利息（元）", headerOrder = 170, columnStyle = ColumnStyleEnum.MONEY)
    private String collectionInterestAmount;

    @SimpleExcelHeader(headerName = "剩余本金（元）", headerOrder = 180, columnStyle = ColumnStyleEnum.MONEY)
    private String principalBalanceAmount;

    @SimpleExcelHeader(headerName = "剩余利息（元）", headerOrder = 190, columnStyle = ColumnStyleEnum.MONEY)
    private String interestBalanceAmount;

}