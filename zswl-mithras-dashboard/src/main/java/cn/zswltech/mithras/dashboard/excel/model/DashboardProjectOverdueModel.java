package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectOverdueModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "逾期期项", headerOrder = 30)
    private Integer phase;

    @SimpleExcelHeader(headerName = "逾期天数（天）", headerOrder = 40)
    private String overdueDuration;

    @SimpleExcelHeader(headerName = "逾期金额（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String overdueAmount;

    @SimpleExcelHeader(headerName = "罚息日利率", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String interestPenaltyDailyRate;

    @SimpleExcelHeader(headerName = "罚息金额（元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String interestPenaltyAmount;

    @SimpleExcelHeader(headerName = "罚息减免金额（元）", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String interestPenaltyReduceAmount;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 90)
    private String clientName;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 100)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "业务模式", headerOrder = 110)
    private String leaseTypeDisplay;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 120)
    private String lesseeNames;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 130)
    private String guarantorNames;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 140)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 150)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 160)
    private String projCosponsorUserNames;

}