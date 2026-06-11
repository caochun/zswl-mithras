package cn.zswltech.mithras.kpi.dto.persistence;

import lombok.Data;

@Data
public class PerformanceTargetResult {

    /**
     * 部门id
     */
    private Long bizDeptId;

    /**
     * 预计投放金额
     */
    private Long targetAmount;

}
