package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DashboardProjectInfoStatisticsListREQ {
    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}
