package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardClientOverviewSettleThreeMonthExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "风控行业分类", headerOrder = 20)
    private String riskControlIndustryClassifyDisplay;

    @SimpleExcelHeader(headerName = "资产五级分类", headerOrder = 30)
    private String assetClassifyResultDisplay;

    @SimpleExcelHeader(headerName = "存量风险敞口（万元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String stockRiskExposure;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 70)
    private String contractCodes;

    @SimpleExcelHeader(headerName = "到期日", headerOrder = 80, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate deadline;

    @SimpleExcelHeader(headerName = "剩余天数（天）", headerOrder = 90)
    private Long remainingDuration;

    @SimpleExcelHeader(headerName = "客户分类", headerOrder = 100)
    private String clientTypeName;

    @SimpleExcelHeader(headerName = "国标行业分类", headerOrder = 110)
    private String industryTypeDisplay;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 120)
    private String projNames;

    @SimpleExcelHeader(headerName = "已收租金（万元）", headerOrder = 130, columnStyle = ColumnStyleEnum.MONEY)
    private String collectionAmount;

    @SimpleExcelHeader(headerName = "已收本金（万元）", headerOrder = 140, columnStyle = ColumnStyleEnum.MONEY)
    private String collectionPrincipalAmount;

    @SimpleExcelHeader(headerName = "已收利息（万元）", headerOrder = 150, columnStyle = ColumnStyleEnum.MONEY)
    private String collectionInterestAmount;

    @SimpleExcelHeader(headerName = "剩余本金（万元）", headerOrder = 160, columnStyle = ColumnStyleEnum.MONEY)
    private String principalBalanceAmount;

    @SimpleExcelHeader(headerName = "剩余利息（万元）", headerOrder = 170, columnStyle = ColumnStyleEnum.MONEY)
    private String interestInterestAmount;

}
