package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardProjectStageStatisticsREQ {
    @ApiModelProperty("客户ID")
    private Long clientId;

    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}
