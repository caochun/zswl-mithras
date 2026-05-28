package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/1/6
 * @description
 */
@Data
public class HeTongShiXiaoStatisticRSP {
    @ApiModelProperty("部门ID")
    private Long bizDeptId;

    @ApiModelProperty("部门名称")
    private String bizDeptName;

    @ApiModelProperty("业务类型")
    private String businessCategory;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("全流程平均耗时（工作日）")
    private String averageDuration;

    @ApiModelProperty("运营经办平均耗时（工作日）")
    private String averageDurationYYJB;

    @ApiModelProperty("运营部平均耗时（工作日）")
    private String averageDurationYYB;

    @ApiModelProperty("法务部平均耗时（工作日）")
    private String averageDurationFWB;

    @ApiModelProperty("财务部平均耗时（工作日）")
    private String averageDurationCWB;
}
