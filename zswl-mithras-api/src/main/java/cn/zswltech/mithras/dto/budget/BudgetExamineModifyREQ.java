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
@ApiModel("预算管理-预算考核编辑-请求体")
public class BudgetExamineModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 逻辑删除，0-未删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除")
    private Integer deleted;

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

    /**
    * 提交时间
    */
    @ApiModelProperty(value = "提交时间")
    private LocalDate submitTime;

}
