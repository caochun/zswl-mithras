package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardOperationApprovalModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "流程id", headerOrder = 10)
    private String flowId;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "项目编号", headerOrder = 30)
    private String projCode;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 40)
    private String contractCode;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 50)
    private String clientName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 60)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 70)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "业务分类", headerOrder = 80)//转枚举
    private String businessGroup;

    @SimpleExcelHeader(headerName = "业务模式", headerOrder = 90)//转枚举
    private String businessModel;

    @SimpleExcelHeader(headerName = "申请时间", headerOrder = 100)
    private String applyTime;

    @SimpleExcelHeader(headerName = "流程结束时间", headerOrder = 110)
    private String endTime;

    @SimpleExcelHeader(headerName = "流程总耗时(工作日)", headerOrder = 120, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal approvalTotalTime;

    @SimpleExcelHeader(headerName = "运营经办到达时间", headerOrder = 130)
    private String operationHandlingArrivalTime;

    @SimpleExcelHeader(headerName = "运营经办提交时间", headerOrder = 140)
    private String operationHandlingSubmitTime;

    @SimpleExcelHeader(headerName = "运营经办耗时(工作日)", headerOrder = 150, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal operationHandlingTotalTime;

    @SimpleExcelHeader(headerName = "运营复核到达时间", headerOrder = 160)
    private String operationReviewArrivalTime;

    @SimpleExcelHeader(headerName = "运营复核提交时间", headerOrder = 170)
    private String operationReviewSubmitTime;

    @SimpleExcelHeader(headerName = "运营复核耗时(工作日)", headerOrder = 180, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal operationReviewTotalTime;

    @SimpleExcelHeader(headerName = "运营部总耗时(工作日)", headerOrder = 190, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal operationTotalTime;

}
