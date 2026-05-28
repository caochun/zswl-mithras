package cn.zswltech.mithras.dto.kpi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配表-列表-返回参数")
public class KpiProjectDistributionListRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("合同编号")
    private String contractCode;

    private Long contractId;

    private Long projectDistributionId;

    private String kpiProjectDistributionVersion;

    private String projClassify;
    private String projSource;
    private String projReviewSource;

    @ApiModelProperty("分配状态")
    private Integer distributionStatus;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("合同开始日期（投放日期）")
    private String contractStartDate;

    @ApiModelProperty("所属部门id")
    private Long belongDeptId;

    @ApiModelProperty("所属部门名称")
    private String belongDeptName;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty("人员分润比")
    private List<KpiProjectDistributionWeightInfo> weightInfoList;

    @ApiModelProperty("部门分润比")
    private List<KpiProjectDistributionDeptWeightInfo> deptWeightInfoList;

    @ApiModelProperty("最新生效月份")
    private String effectMonth;

    // 增加几个导出子段，不返回给前端
    // 【流程类型】：绩效考核项目分配创建、绩效考核项目分配变更、绩效考核项目分配移交
    @JsonIgnore
    @ApiModelProperty("流程类型")
    private String processType;

    //【流程ID】：流程对应的id
    @JsonIgnore
    @ApiModelProperty("流程ID")
    private String processId;

    //【最后审批通过时间】：流程审批通过时间
    @JsonIgnore
    @ApiModelProperty("最后审批通过时间")
    private LocalDateTime lastApproveTime;

}
