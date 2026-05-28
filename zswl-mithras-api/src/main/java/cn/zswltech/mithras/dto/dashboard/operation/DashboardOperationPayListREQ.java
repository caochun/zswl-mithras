package cn.zswltech.mithras.dto.dashboard.operation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DashboardOperationPayListREQ extends DashboardOperationBaseREQ {

    @ApiModelProperty("部门id")
    private List<Long> bizDeptIdList;
}
