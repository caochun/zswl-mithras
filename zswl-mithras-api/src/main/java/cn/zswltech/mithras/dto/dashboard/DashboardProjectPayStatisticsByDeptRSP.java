package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/6/23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayStatisticsByDeptRSP extends DashboardProjectPayStatisticsRSP {
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    public static DashboardProjectPayStatisticsByDeptRSP createNoData(Long bizDeptId, String bizDeptName) {
        DashboardProjectPayStatisticsByDeptRSP rsp = new DashboardProjectPayStatisticsByDeptRSP();
        rsp.setBizDeptId(bizDeptId);
        rsp.setBizDeptName(bizDeptName);
        rsp.setPayContractQuantity(0);
        return rsp;
    }
}
