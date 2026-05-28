package cn.zswltech.mithras.dto.financeprojectdistribution;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author bigbear
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("财务-项目分配-部门投放分配-比重信息")
public class FinanceProjectDistributionDeptLaunchWeightInfo extends ListBaseRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标识合同的业务部门")
    private Boolean isBusinessDept;

    @ApiModelProperty("分配比重目标")
    private Long weightTarget;

    // 分配比重目标名称
    @ApiModelProperty("分配比重目标名称")
    private String weightTargetName;

    // 分配比重值
    @ApiModelProperty("分配比重值")
    private Integer weightValue;
}