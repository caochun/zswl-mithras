package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardFundFinanceBalanceModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 20)
    private String financingCode;

    @SimpleExcelHeader(headerName = "融资机构/产品名称", headerOrder = 30)
    private String orgName;

    @SimpleExcelHeader(headerName = "融资类别", headerOrder = 50)
    private String financingTypeDisplay;

    @SimpleExcelHeader(headerName = "融资方式", headerOrder = 60)
    private String businessType;

    @SimpleExcelHeader(headerName = "融资金额(元)", headerOrder = 70)
    private String loanAmount;

    @SimpleExcelHeader(headerName = "期限(月)", headerOrder = 80)
    private String duration;

    @SimpleExcelHeader(headerName = "综合利率", headerOrder = 90)
    private String comprehensiveInterestRate;

    @SimpleExcelHeader(headerName = "手续费(元)", headerOrder = 100)
    private String commission;

    @SimpleExcelHeader(headerName = "借款年利率", headerOrder = 110)
    private String interestRate;

    @SimpleExcelHeader(headerName = "利率方式", headerOrder = 120)
    private String interestRateWay;

    @SimpleExcelHeader(headerName = "剩余本金(元)", headerOrder = 130)
    private String remainingAmount;

    @SimpleExcelHeader(headerName = "剩余期限(年)", headerOrder = 140)
    private String remainingDuration;

    @SimpleExcelHeader(headerName = "质押资产合同编号", headerOrder = 150)
    private String relatedContractCodeList;
}