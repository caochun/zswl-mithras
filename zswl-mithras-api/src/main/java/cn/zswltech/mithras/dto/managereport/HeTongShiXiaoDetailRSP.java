package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/1/5
 * @description
 */
@Data
public class HeTongShiXiaoDetailRSP {
    @ApiModelProperty("流程ID")
    private String processInstanceId;

    @ApiModelProperty("合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("项目主办ID")
    private Long sponsorUserId;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty("流程开始时间")
    private LocalDateTime processStartTime;

    @ApiModelProperty("流程结束时间")
    private LocalDateTime processEndTime;

    @ApiModelProperty("部门ID")
    private Long bizDeptId;

    @ApiModelProperty("部门名称")
    private String bizDeptName;

    @ApiModelProperty("业务类型")
    private String businessCategory;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("全流程耗时（工作日小时）")
    private String duration;

    @ApiModelProperty("运营经办耗时（工作日小时）")
    private String durationYYJB;

    @ApiModelProperty("运营复核耗时（工作日小时）")
    private String durationYYFH;

    @ApiModelProperty("运营负责人耗时（工作日小时）")
    private String durationYYFZR;

    @ApiModelProperty("法务经理耗时（工作日小时）")
    private String durationFWJL;

    @ApiModelProperty("法务负责人耗时（工作日小时）")
    private String durationFWFZR;

    @ApiModelProperty("财务主管耗时（工作日小时）")
    private String durationCWZG;
}
