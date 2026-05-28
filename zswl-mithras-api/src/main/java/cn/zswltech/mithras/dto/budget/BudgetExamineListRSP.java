package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 预算管理-预算考核
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核列表-返回体")
public class BudgetExamineListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 考核名称
    */
    @ApiModelProperty(value = "考核名称")
    private String examineName;

    /**
    * 考核年份
    */
    @ApiModelProperty(value = "考核年份")
    private Integer examineYear;

    /**
    * 考核月份
    */
    @ApiModelProperty(value = "考核月份")
    private Integer examineMonth;

    /**
    * 审批状态
    */
    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;

    /**
    * 提交人id
    */
    @ApiModelProperty(value = "提交人id")
    private Long submitUserId;

    @ApiModelProperty(value = "提交人名称")
    private String submitUserName;

    /**
    * 提交时间
    */
    @ApiModelProperty(value = "提交时间")
    private LocalDate submitTime;

}
