package cn.zswltech.mithras.service.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/8/28/10:10
 * @description
 */
@Data
public class AfterLeaseCheckPlanListExcelModel {

    @ExcelProperty(value = "计划名称", index = 0)
    private String planName;

    @ExcelProperty(value = "计划类型", index = 1)
    private String planType;

    @ExcelProperty(value = "风险敞口（万元）", index = 2)
    private String riskExposure;

    @ExcelProperty(value = "剩余本金（万元）", index = 3)
    private String remainingPrincipal;

    @ExcelProperty(value = "项目主办", index = 4)
    private String projSponsorUserName;

    @ExcelProperty(value = "业务部门", index = 5)
    private String bizDeptName;

    @ExcelProperty(value = "协查风控经理", index = 6)
    private String riskControlManagerName;

    @ExcelProperty(value = "本次检查形式", index = 7)
    private String checkWay;

    @ExcelProperty(value = "本次租后截止时间", index = 8)
    @DateTimeFormat("yyyy-MM-dd")
    private String deadLine;

    @ExcelProperty(value = "上次跟进形式", index = 9)
    private String lastCheckWay;

    @ExcelProperty(value = "计划状态", index = 10)
    private String planStatus;

    @ExcelProperty(value = "审批状态", index = 11)
    private String approvalStatus;

    @ExcelProperty(value = "当前审批人", index = 12)
    private String curAssigneeNames;

    @ExcelProperty(value = "检查所属时间", index = 13)
    private String checkBelongTime;

    @ExcelProperty(value = "创建时间", index = 14)
    @DateTimeFormat("yyyy-MM-dd HH:mm")
    private String createTime;

    @ExcelProperty(value = "变更时间", index = 15)
    @DateTimeFormat("yyyy-MM-dd HH:mm")
    private String updateTime;

}
