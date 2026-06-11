package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardFundFinanceRepayModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 50)
    private String financingCode;

    @SimpleExcelHeader(headerName = "机构名称/产品名称", headerOrder = 70)
    private String orgName;

    @SimpleExcelHeader(headerName = "贷款金额(万元)", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String loanAmount;

    @SimpleExcelHeader(headerName = "剩余贷款金额(万元)", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String loanBalanceAmount;

    @SimpleExcelHeader(headerName = "本月计划应还金额(元)", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String repayTotalAmount;

    @SimpleExcelHeader(headerName = "本月应还本金(元)", headerOrder = 110, columnStyle = ColumnStyleEnum.MONEY)
    private String repayPrincipalAmount;

    @SimpleExcelHeader(headerName = "本月应还利息(元)", headerOrder = 120, columnStyle = ColumnStyleEnum.MONEY)
    private String repayInterestAmount;

    @SimpleExcelHeader(headerName = "本月应还日期", headerOrder = 130)
    private String repayDate;
//
//    @SimpleExcelHeader(headerName = "关联项目名称", headerOrder = 140)
//    private String relatedProjName;
//
//    @SimpleExcelHeader(headerName = "关联合同编号", headerOrder = 150)
//    private String relatedContractCode;
//
//    @SimpleExcelHeader(headerName = "租金回笼金额（元）", headerOrder = 160, columnStyle = ColumnStyleEnum.MONEY)
//    private String rentPlanCollectionAmount;
//
//    @SimpleExcelHeader(headerName = "租金回笼日期", headerOrder = 170)
//    private LocalDate rentPlanCollectionDate;

    @SimpleExcelHeader(headerName = "本月已还金额（元）", headerOrder = 180, columnStyle = ColumnStyleEnum.MONEY)
    private String actualRepayAmount;

    @SimpleExcelHeader(headerName = "本月未还金额（元）", headerOrder = 190, columnStyle = ColumnStyleEnum.MONEY)
    private String repayBalanceAmount;

    @SimpleExcelHeader(headerName = "本月支付日期", headerOrder = 200)
    private String actualRepayDate;

//    @SimpleExcelHeader(headerName = "核销状态", headerOrder = 210)
//    private String writeOffStateDisplay;

//    @SimpleExcelHeader(headerName = "账户性质", headerOrder = 220)
//    private String bankAccountTypeDisplay;
}