package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardFundFinanceFundsModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 20)
    private String financingCode;

    @SimpleExcelHeader(headerName = "融资机构/产品名称", headerOrder = 30)
    private String orgName;

    @SimpleExcelHeader(headerName = "融资类别", headerOrder = 50)
    private String financingTypeDisplay;

    @SimpleExcelHeader(headerName = "融资金额(元)", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String loanAmount;

    @SimpleExcelHeader(headerName = "剩余金额(元)", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String remainingPrincipleAmount;

    @SimpleExcelHeader(headerName = "综合资金成本", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String comprehensiveInterestRate;

    @SimpleExcelHeader(headerName = "起息日", headerOrder = 101, columnStyle = ColumnStyleEnum.DATE)
    private String actualLoanDateStr;

    @SimpleExcelHeader(headerName = "到期日", headerOrder = 102, columnStyle = ColumnStyleEnum.DATE)
    private String actualExpireDateStr;

    @SimpleExcelHeader(headerName = "质押合同", headerOrder = 110)
    private String relatedContractCodeList;
}