package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 绩效考核-项目分配记录表
 * @author vico
 * @date 2024-09-27
 */
@Data
@ApiModel("绩效考核-项目分配记录表新增-请求体")
public class KpiProjectDistributionRecordAddREQ {

    /**
    * 批次号
    */
    @ApiModelProperty(value = "批次号")
    private Integer batchNumber;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 分配状态，0-未分配，1-已分配
    */
    @ApiModelProperty(value = "分配状态，0-未分配，1-已分配")
    private Integer distributionStatus;

    /**
    * 审批状态
    */
    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;

    /**
    * 项目类别
    */
    @ApiModelProperty(value = "项目类别")
    private String projClassify;

    /**
    * 项目来源
    */
    @ApiModelProperty(value = "项目来源")
    private String projSource;

    /**
    * 合同开始时间
    */
    @ApiModelProperty(value = "合同开始时间")
    private LocalDate contractStartDate;

    /**
    * 合同结束时间
    */
    @ApiModelProperty(value = "合同结束时间")
    private LocalDate contractEndDate;

    /**
    * 利润所属部门id
    */
    @ApiModelProperty(value = "利润所属部门id")
    private Long profitBelongDeptId;

    /**
    * 团队长用户id
    */
    @ApiModelProperty(value = "团队长用户id")
    private Long teamLeaderId;

    /**
    * 生效年份
    */
    @ApiModelProperty(value = "生效年份")
    private Integer effectYear;

    /**
    * 生效月份
    */
    @ApiModelProperty(value = "生效月份")
    private Integer effectMonth;

}
