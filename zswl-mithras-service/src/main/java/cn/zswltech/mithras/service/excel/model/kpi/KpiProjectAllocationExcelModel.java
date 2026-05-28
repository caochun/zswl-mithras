package cn.zswltech.mithras.service.excel.model.kpi;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2023/8/21
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配表-导出")
public class KpiProjectAllocationExcelModel {
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "分配状态")
    private Integer distributionStatus;

    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;

    @ExcelProperty(value = "项目名称")
    private String projName;

    @ExcelProperty(value = "合同开始日期（投放日期）")
    private String contractStartDate;

    @ExcelProperty(value = "所属部门名称")
    private String belongDeptName;

    @ExcelProperty(value = "项目主办名称")
    private String sponsorUserName;

    @ExcelProperty(value = "分润比")
    private String weightInfoList;
}
