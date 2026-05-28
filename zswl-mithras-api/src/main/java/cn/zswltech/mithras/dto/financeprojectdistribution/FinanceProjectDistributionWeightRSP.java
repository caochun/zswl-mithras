package cn.zswltech.mithras.dto.financeprojectdistribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@Data
@ApiModel("财务-项目分配-分配比重-列表-返回参数")
public class FinanceProjectDistributionWeightRSP {
    @ApiModelProperty("项目分配id")
    private Long projectDistributionId;

    @ApiModelProperty("部门-分配比重信息")
    private List<FinanceProjectDistributionDeptWeightInfo> deptWeightInfoList;

    @ApiModelProperty("项目投放分配比")
    private List<FinanceProjectDistributionDeptLaunchWeightInfo> deptLaunchWeightInfoList;
}
