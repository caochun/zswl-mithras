package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardFundFinanceLoanInfoModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "融资类别", headerOrder = 30)
    private String financingTypeDisplay;

    @SimpleExcelHeader(headerName = "融资机构/产品名称", headerOrder = 40)
    private String orgName;

    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 41)
    private String financingCode;

    @SimpleExcelHeader(headerName = "融资金额（元）", headerOrder = 42)
    private String loanAmount;

    @SimpleExcelHeader(headerName = "融资余额（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String balanceAmount;

    @SimpleExcelHeader(headerName = "合同利率（%）", headerOrder = 60)
    private String interestRate;

    @SimpleExcelHeader(headerName = "综合资金成本（%）", headerOrder = 61)
    private String comprehensiveFinancingCost;

    @SimpleExcelHeader(headerName = "起息日", headerOrder = 62, columnStyle = ColumnStyleEnum.DATE)
    private String actualLoanDateStr;

    @SimpleExcelHeader(headerName = "到期日", headerOrder = 63, columnStyle = ColumnStyleEnum.DATE)
    private String actualExpireDateStr;

    @SimpleExcelHeader(headerName = "期限（月）", headerOrder = 70)
    private String duration;

    @SimpleExcelHeader(headerName = "还款方式", headerOrder = 80)
    private String repayWay;

    @SimpleExcelHeader(headerName = "质押资产合同编号", headerOrder = 90)
    private String relatedContractCodeList;
}