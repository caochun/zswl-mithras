package cn.zswltech.mithras.dto.financeprojectdistribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 财务部门-项目投放分配比重版本表
 * @author hspcadmin
 * @date 2025-09-29
 */
@Data
@ApiModel("财务部门-项目投放分配比重版本表删除-请求体")
public class FinanceProjectDistributionDeptLaunchWeightLibRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
