package cn.zswltech.mithras.kpi.bo;

import cn.zswltech.mithras.service.enums.kpi.KpiProjectClassifyEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectSourceDistributionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配表-列表-返回参数")
public class KpiProjectDistributionRecordBo {
    @ApiModelProperty("id")
    private Long id;

    private Integer batchNumber;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("分配状态")
    private Integer distributionStatus;

    private Long projectDistributionId;

    private String kpiProjectDistributionVersion;

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

    /**
     * 项目类别 {@link KpiProjectClassifyEnum#name()}
     */
    private String projClassify;

    /**
     * 项目来源 {@link KpiProjectSourceDistributionEnum#name()}
     */
    private String projSource;

    @ApiModelProperty("分润比")
    private List<KpiProjectDistributionWeightInfoRecordBo> weightInfoList;
}
