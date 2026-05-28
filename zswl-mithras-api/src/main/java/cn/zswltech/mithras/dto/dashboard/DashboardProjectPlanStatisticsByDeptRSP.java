package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPlanStatisticsByDeptRSP extends DashboardProjectPlanStatisticsRSP {
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    public static DashboardProjectPlanStatisticsByDeptRSP createNoData(Long bizDeptId, String bizDeptName) {
        DashboardProjectPlanStatisticsByDeptRSP rsp = new DashboardProjectPlanStatisticsByDeptRSP();
        rsp.setBizDeptId(bizDeptId);
        rsp.setBizDeptName(bizDeptName);
        rsp.setPayContractQuantity(0);
        rsp.setSortedField(new BigDecimal("0"));
        return rsp;
    }
}
