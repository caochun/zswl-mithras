package cn.zswltech.mithras.dto.dashboard.operation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DashboardOperationCapacityListREQ extends DashboardOperationBaseREQ {

    @ApiModelProperty("部门id")
    private List<Long> bizDeptIdList;

    @ApiModelProperty("项目阶段 DashboardProjStageEnum")
    private String projStage;

}
