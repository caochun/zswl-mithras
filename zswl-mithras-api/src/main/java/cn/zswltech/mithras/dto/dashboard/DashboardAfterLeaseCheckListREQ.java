package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAfterLeaseCheckListREQ extends PageReq {
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty("检查方式code")
    private String checkWayCode;

    @ApiModelProperty("审批状态 DashboardAfterLeaseCheckStatueEnum")
    private String approvalStatus;

    private String checkPlanStatus;

}
