package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPlanListREQ extends DashboardProjectPlanStatisticsREQ {
    @ApiModelProperty("部门id")
    private Long bizDeptId;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("合同编号")
    private String contractCode;
}
