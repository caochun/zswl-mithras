package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2025/4/9 09:58
 * @description
 */
@Data
@ApiModel(value = "绩效考核-项目分配-部门分配-比重信息请求体")
public class KpiProjectDistributionDeptWeightListREQ {

    @ApiModelProperty(value = "项目分配id")
    @NotNull(message = "项目分配id不能为空")
    private Long projectDistributionId;
}
