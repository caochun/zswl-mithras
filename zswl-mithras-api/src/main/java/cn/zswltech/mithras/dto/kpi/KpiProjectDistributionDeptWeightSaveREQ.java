package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bigbear
 * @date 2025/4/9 09:58
 * @description
 */
@Data
@ApiModel(value = "绩效考核-项目分配-部门分配-比重信息请求体")
public class KpiProjectDistributionDeptWeightSaveREQ {

    @ApiModelProperty(value = "项目分配id")
    @NotNull(message = "项目分配id不能为空")
    private Long projectDistributionId;

    @ApiModelProperty(value = "本次修改的所有数据")
    @NotEmpty(message = "本次修改的所有数据不能为空")
    private List<KpiProjectDistributionDeptWeightInfo> deptWeightInfoList;

    @ApiModelProperty(value = "部门投放分配比所有数据")
    @NotEmpty(message = "部门投放分配比不能为空")
    private List<KpiProjectDistributionDeptLaunchWeightInfo> deptLaunchWeightInfoList;//部门投放分配比
}
