package cn.zswltech.mithras.dto.kpi;

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
@ApiModel("绩效考核-项目分配-分配比重-列表-返回参数")
public class KpiProjectDistributionWeightRSP {
    @ApiModelProperty("项目分配id")
    private Long projectDistributionId;

    @ApiModelProperty("生效年份")
    private Integer effectYear;

    @ApiModelProperty("生效月份")
    private Integer effectMonth;

    @ApiModelProperty("人员-分配比重信息")
    private List<KpiProjectDistributionWeightInfo> weightInfoList;

    @ApiModelProperty("部门-分配比重信息")
    private List<KpiProjectDistributionDeptWeightInfo> deptWeightInfoList;

    @ApiModelProperty("项目投放分配比")
    private List<KpiProjectDistributionDeptLaunchWeightInfo> deptLaunchWeightInfoList;
}
