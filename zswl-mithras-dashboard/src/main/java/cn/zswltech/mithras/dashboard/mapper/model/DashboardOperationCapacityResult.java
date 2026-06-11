package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;


@Data
public class DashboardOperationCapacityResult {

    private Long id;
    private String type;
    private Long bizDeptId;
    private String riskControl;
    private Long amountLong;


}
