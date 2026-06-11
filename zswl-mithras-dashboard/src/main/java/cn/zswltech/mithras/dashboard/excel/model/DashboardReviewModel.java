package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DashboardReviewModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "部门", headerOrder = 10)
    private String deptName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "项目金额（万元）", headerOrder = 30)
    private Double projAmount;

    @SimpleExcelHeader(headerName = "批复金额（万元）", headerOrder = 40)
    private Double approvalAmount;

    @SimpleExcelHeader(headerName = "业务组分类", headerOrder = 50)
    private String businessGroup;

    @SimpleExcelHeader(headerName = "召开时间", headerOrder = 60, columnStyle = ColumnStyleEnum.DATE)
    private LocalDateTime convokeTime;

    @SimpleExcelHeader(headerName = "评审结果", headerOrder = 60)
    private String projCount;

    @SimpleExcelHeader(headerName = "评审结果", headerOrder = 60)
    private String reviewResult;

}
