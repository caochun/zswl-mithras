package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardClientOverviewAllExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "风控行业分类", headerOrder = 20)
    private String riskControlIndustryClassifyDisplay;

    @SimpleExcelHeader(headerName = "资产五级分类", headerOrder = 30)
    private String assetClassifyResultDisplay;

    @SimpleExcelHeader(headerName = "授信总金额（万元）", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String creditAmount;

    @SimpleExcelHeader(headerName = "剩余本金（万元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String principalBalanceAmount;

    @SimpleExcelHeader(headerName = "存量风险敞口（万元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String stockRiskExposure;

    @SimpleExcelHeader(headerName = "客户分类", headerOrder = 70)
    private String clientTypeName;

    @SimpleExcelHeader(headerName = "国标行业分类", headerOrder = 80)
    private String industryTypeDisplay;

    @SimpleExcelHeader(headerName = "企业性质", headerOrder = 90)
    private String enterpriseNatureDisplay;

    @SimpleExcelHeader(headerName = "省份", headerOrder = 100)
    private String provinceDisplay;

    @SimpleExcelHeader(headerName = "所属部门", headerOrder = 110)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "所属主办", headerOrder = 120)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "创建人", headerOrder = 130)
    private String creatorName;

}
