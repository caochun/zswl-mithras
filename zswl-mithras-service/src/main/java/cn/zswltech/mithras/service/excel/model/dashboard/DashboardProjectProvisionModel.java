package cn.zswltech.mithras.service.excel.model.dashboard;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectProvisionModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "合同金额", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private String contractAmount;

    @SimpleExcelHeader(headerName = "剩余本金（元）", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String principalBalance;

    @SimpleExcelHeader(headerName = "保证金（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String earnest;

    @SimpleExcelHeader(headerName = "风险敞口（元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String stockRiskExposure;

    @SimpleExcelHeader(headerName = "剩余期限（月）", headerOrder = 70)
    private String remainingDurationMonths;

    @SimpleExcelHeader(headerName = "拨备金额（元）", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String provision;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 90)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "项目类型", headerOrder = 100)
    private String projectClassifyDisplay;

    @SimpleExcelHeader(headerName = "业务部门名称", headerOrder = 110)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "到期日", headerOrder = 120)
    private LocalDate deadline;

    @SimpleExcelHeader(headerName = "风险等级", headerOrder = 130)
    private String assetsClassifyDisplay;

    @SimpleExcelHeader(headerName = "计提比例", headerOrder = 140, columnStyle = ColumnStyleEnum.MONEY)
    private String provisionRate;
}