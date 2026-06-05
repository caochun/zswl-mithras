package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "投放日期", headerOrder = 10)
    private LocalDate actualPayDate;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 20)
    private String clientName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 30)
    private String contractCode;

    @SimpleExcelHeader(headerName = "风控行业分类", headerOrder = 40)
    private String riskControlIndustryClassifyDisplay;

    @SimpleExcelHeader(headerName = "风险策略", headerOrder = 50)
    private String riskStrategy;

    @SimpleExcelHeader(headerName = "投放金额（元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String actualPayAmount;

    @SimpleExcelHeader(headerName = "项目期限（月）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String duration;

    @SimpleExcelHeader(headerName = "IRR（%）", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String actualIrr;

    @SimpleExcelHeader(headerName = "合同利率（%）", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String interestRate;

    @SimpleExcelHeader(headerName = "咨询费率（%）", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String consultingFeeRate;

    @SimpleExcelHeader(headerName = "手续费率（%）", headerOrder = 110, columnStyle = ColumnStyleEnum.MONEY)
    private String commissionRate;

    @SimpleExcelHeader(headerName = "保证金金额（元）", headerOrder = 120, columnStyle = ColumnStyleEnum.MONEY)
    private String earnest;

    @SimpleExcelHeader(headerName = "地区", headerOrder = 130)
    private String regionalProjectClassifyDisplay;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 140)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 150)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 160)
    private String projCosponsorUserNames;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 170)
    private String bizDeptName;

}