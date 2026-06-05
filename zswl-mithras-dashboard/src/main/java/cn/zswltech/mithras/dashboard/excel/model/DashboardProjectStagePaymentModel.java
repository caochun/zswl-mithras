package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardProjectStagePaymentModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "风控行业分类", headerOrder = 20)
    private String riskControlIndustryClassifyDisplay;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 30)
    private String contractCode;

    @SimpleExcelHeader(headerName = "申请付款日期", headerOrder = 40, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate applyPayDate;

    @SimpleExcelHeader(headerName = "申请付款金额", headerOrder = 50, columnStyle = ColumnStyleEnum.DATE)
    private String applyPayAmount;

    @SimpleExcelHeader(headerName = "审批状态", headerOrder = 60)
    private String paymentProcessStatusDisplay;

    @SimpleExcelHeader(headerName = "申请状态", headerOrder = 70)
    private String paymentStatusDisplay;

    @SimpleExcelHeader(headerName = "当前阶段停留天数（工作日）", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String lengthOfStay;

    @SimpleExcelHeader(headerName = "合同金额（元）", headerOrder = 85, columnStyle = ColumnStyleEnum.MONEY)
    private String contractAmount;

    @SimpleExcelHeader(headerName = "租赁期限(月)", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String leaseDuration;

    @SimpleExcelHeader(headerName = "租赁利率", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String interestRate;

    @SimpleExcelHeader(headerName = "国标行业分类", headerOrder = 110)
    private String industryTypeDisplay;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 120)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 130)
    private String lesseeNames;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 140)
    private String guarantorNames;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 150)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 160)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 170)
    private String projCosponsorUserNames;

    @SimpleExcelHeader(headerName = "申请日期", headerOrder = 180)
    private String applyTime;
}
