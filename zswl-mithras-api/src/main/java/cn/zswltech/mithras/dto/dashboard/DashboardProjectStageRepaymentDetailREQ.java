package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@Data
public class DashboardProjectStageRepaymentDetailREQ {
    private String contractCode;
    private String projName;
    private Long totalRentBalanceFrom;
    private Long totalRentBalanceTo;
    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}
