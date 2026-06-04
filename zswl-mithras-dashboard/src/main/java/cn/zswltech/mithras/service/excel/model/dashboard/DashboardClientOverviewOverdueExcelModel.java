package cn.zswltech.mithras.service.excel.model.dashboard;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardClientOverviewOverdueExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 30)
    private String contractCode;

    @SimpleExcelHeader(headerName = "逾期期项", headerOrder = 40)
    private Integer phase;

    @SimpleExcelHeader(headerName = "逾期金额(万元)", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String overdueAmount;

    @SimpleExcelHeader(headerName = "罚息日利率", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String penaltyInterestRate;

    @SimpleExcelHeader(headerName = "罚息金额(万元)", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String penaltyInterestAmount;

    @SimpleExcelHeader(headerName = "罚息减免金额(万元)", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private Long penaltyInterestReductionAmount;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 90)
    private String bizType;

    @SimpleExcelHeader(headerName = "业务模式", headerOrder = 100)
    private String bizModel;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 110)
    private String tenantry;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 120)
    private String guarantorList;

    @SimpleExcelHeader(headerName = "所属部门", headerOrder = 130)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "所属主办", headerOrder = 140)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 150)
    private String projCosponsorUserNames;

}
