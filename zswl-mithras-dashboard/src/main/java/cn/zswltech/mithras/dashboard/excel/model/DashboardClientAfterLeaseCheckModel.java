package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DashboardClientAfterLeaseCheckModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "租后检查计划名称", headerOrder = 20)
    private String checkPlanName;

    @SimpleExcelHeader(headerName = "计划类型", headerOrder = 30)
    private String checkPlanTypeDisplay;

    @SimpleExcelHeader(headerName = "本次检查形式", headerOrder = 40)
    private String checkWayDisplay;

    @SimpleExcelHeader(headerName = "本次租后检查截止时间", headerOrder = 50, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate checkDate;

    @SimpleExcelHeader(headerName = "计划状态", headerOrder = 60)
    private String checkPlanStatusDisplay;

    @SimpleExcelHeader(headerName = "审批状态", headerOrder = 70)
    private String reportProcessStatusDisplay;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 80)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 90)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "创建时间", headerOrder = 100, columnStyle = ColumnStyleEnum.DATE)
    private LocalDateTime createTime;

    @SimpleExcelHeader(headerName = "更新时间", headerOrder = 110, columnStyle = ColumnStyleEnum.DATE)
    private LocalDateTime updateTime;
}
