package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("绩效考核-项目分配表-列表-请求参数")
public class KpiProjectDistributionListREQ extends PageReq {
    @ApiModelProperty("分配状态")
    @NotNull(message = "分配状态不能为空")
    private Integer distributionStatus;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("分润比项目主办id")
    private Long sponsorUserIdWeight;

    @ApiModelProperty("分润比项目协办id")
    private Long projCosponsorUserIdWeight;

    @ApiModelProperty("分润比业务部门id")
    private Long bizDeptIdWeight;

    private List<Long> contractIds;
}
