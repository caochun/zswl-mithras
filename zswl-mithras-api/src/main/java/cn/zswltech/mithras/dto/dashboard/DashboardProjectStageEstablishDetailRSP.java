package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/6/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageEstablishDetailRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("立项类型")
    private String projEstablishType;
    @ApiModelProperty("立项状态code")
    private String projEstablishStatusCode;
    @ApiModelProperty("立项状态display")
    private String projEstablishStatusDisplay;
    @ApiModelProperty("立项审批状态code")
    private String projEstablishProcessStatusCode;
    @ApiModelProperty("立项审批状态display")
    private String projEstablishProcessStatusDisplay;
}
