package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 绩效考核-部门-项目投放分配比重表
 * @author hspcadmin
 * @date 2025-09-29
 */
@Data
@ApiModel("绩效考核-部门-项目投放分配比重表删除-请求体")
public class KpiProjectDistributionDeptLaunchWeightRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
