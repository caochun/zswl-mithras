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
public class DashboardProjectStageContractDetailRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("合同状态code")
    private String contractStatusCode;
    @ApiModelProperty("合同状态display")
    private String contractStatusDisplay;
    @ApiModelProperty("合同审批状态code")
    private String contractProcessStatusCode;
    @ApiModelProperty("合同审批状态display")
    private String contractProcessStatusDisplay;
    @ApiModelProperty("合同金额")
    private ValueUnitDTO contractAmount;
    @ApiModelProperty("租赁利率")
    private ValueUnitDTO interestRate;
    @ApiModelProperty("租赁期限")
    private ValueUnitDTO leaseDuration;
    @ApiModelProperty("签约方式（预留）")
    private String signContractWay;
}
