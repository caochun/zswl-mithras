package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardOperationContractReturnModel extends ExcelModel {

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

    //运营总退回次数
    @SimpleExcelHeader(headerName = "运营总退回次数", headerOrder = 120)
    private Integer yunYingReturnCount;

    @SimpleExcelHeader(headerName = "运营经办", headerOrder = 130)
    private String yunYingGuanLiName;

    @SimpleExcelHeader(headerName = "运营复核", headerOrder = 140)
    private String yunYingGuanLiReviewName;

    @SimpleExcelHeader(headerName = "退回原因", headerOrder = 150)
    private String reasonReturn;
}
