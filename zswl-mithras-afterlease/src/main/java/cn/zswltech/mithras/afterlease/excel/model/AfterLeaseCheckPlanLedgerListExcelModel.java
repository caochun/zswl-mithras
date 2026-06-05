package cn.zswltech.mithras.afterlease.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

@Data
public class AfterLeaseCheckPlanLedgerListExcelModel {

    @ExcelProperty(value = "租后检查计划名称", index = 0)
    private String planName;

    @ExcelProperty(value = "项目主办", index = 1)
    private String belongSponsorUserName;

    @ExcelProperty(value = "业务部门", index = 2)
    private String belongDeptName;

    @ExcelProperty(value = "资产经理", index = 3)
    private String riskManagerName;

    @ExcelProperty(value = "检查形式", index = 4)
    private String checkWay;

    @ExcelProperty(value = "检查报告模板", index = 5)
    private String reportType;

    @ExcelProperty(value = "租后截止日期", index = 6)
    @DateTimeFormat("yyyy-MM-dd")
    private String deadLine;

    @ExcelProperty(value = "现场检查日期", index = 7)
    @DateTimeFormat("yyyy-MM-dd")
    private String checkTime;

    @ExcelProperty(value = "报告提交日期", index = 8)
    @DateTimeFormat("yyyy-MM-dd")
    private String commitTime;

    @ExcelProperty(value = "当前状态", index = 9)
    private String checkStatus;

    @ExcelProperty(value = "是否逾期", index = 10)
    private String overdue;

    @ExcelProperty(value = "逾期天数", index = 11)
    private String overdueDays;

}
