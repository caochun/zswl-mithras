package cn.zswltech.mithras.dto.dashboard.operation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class DashboardOperationCapacityStatisticsREQ extends DashboardOperationBaseREQ {

    @ApiModelProperty("项目阶段 DashboardProjStageEnum")
    private String projStage;

}
