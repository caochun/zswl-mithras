package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AfterLeaseClientPlanRSP {
    private Long clientId;

    private String clientName;

    @ApiModelProperty("计划名称")
    private String planName;

    /**
     * 主办id
     */
    @ApiModelProperty("主办id")
    private Long belongSponsorId;

    private String belongSponsorName;

    @ApiModelProperty("所属部门")
    private Long belongDeptId;

    @ApiModelProperty("检查形式 AfterLeaseCheckWayEnum")
    private String checkWay;

    private Integer term;

    @ApiModelProperty ("租后检查截止日")
    private LocalDate deadLine;

    @ApiModelProperty("截止日期标签 AfterLeaseDeadlineLabelEnum#name")
    private List<String> deadlineLabel;

    @ApiModelProperty("下一次检查日期")
    private LocalDate nextCheckDate;

    @ApiModelProperty("检查模板")
    private String reportType;

    @ApiModelProperty("风险敞口")
    private Long stockRiskExposure;
}
