package cn.zswltech.mithras.service.overdue.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 11:18
 */
@Data
@ApiModel(value = "诉讼审判信息")
public class LitigationTrialInfoDto {

    private Long id;
    @ApiModelProperty(value = "诉讼登记id")
    private Long lrId;
    @ApiModelProperty(value = "一审案件编号")
    private String fcaseNo;
    @ApiModelProperty(value = "一审立案时间")
    private LocalDate ffilingDate;
    @ApiModelProperty(value = "一审受理法院")
    private String facceptingCourt;
    @ApiModelProperty(value = "一开庭日期")
    private LocalDate fhearingDate;
    @ApiModelProperty(value = "一审判决日期")
    private LocalDate fjudgmentDate;
    @ApiModelProperty(value = "二审案件编号")
    private String scaseNo;
    @ApiModelProperty(value = "二审立案时间")
    private LocalDate sfilingDate;
    @ApiModelProperty(value = "二审受理法院")
    private String sacceptingCourt;
    @ApiModelProperty(value = "二审开庭日期")
    private LocalDate shearingDate;
    @ApiModelProperty(value = "二审判决日期")
    private LocalDate sjudgmentDate;
    @ApiModelProperty(value = "再审案件编号")
    private String tcaseNo;
    @ApiModelProperty(value = "再审立案时间")
    private LocalDate tfilingDate;
    @ApiModelProperty(value = "再审受理法院")
    private String tacceptingCourt;
    @ApiModelProperty(value = "再审开庭日期")
    private LocalDate thearingDate;
    @ApiModelProperty(value = "再审判决日期")
    private LocalDate tjudgmentDate;
    @ApiModelProperty(value = "执行案号")
    private String executionNo;
    @ApiModelProperty(value = "执行日期")
    private LocalDate executionDate;
    @ApiModelProperty(value = "保全完成日期")
    private LocalDate preservationCompletionDate;
    @ApiModelProperty(value = "查封到期日期")
    private LocalDate sealingExpirationDate;
}
