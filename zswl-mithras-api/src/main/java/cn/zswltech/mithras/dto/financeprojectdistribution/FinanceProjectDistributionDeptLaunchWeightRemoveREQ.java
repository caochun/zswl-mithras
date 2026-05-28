package cn.zswltech.mithras.dto.financeprojectdistribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 财务-部门-项目投放分配比重表
 * @author hspcadmin
 * @date 2025-09-29
 */
@Data
@ApiModel("财务-部门-项目投放分配比重表删除-请求体")
public class FinanceProjectDistributionDeptLaunchWeightRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
