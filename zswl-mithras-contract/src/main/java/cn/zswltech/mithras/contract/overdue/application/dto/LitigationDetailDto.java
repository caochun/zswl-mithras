package cn.zswltech.mithras.contract.overdue.application.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 10:52
 */
@Data
public class LitigationDetailDto {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "合同id")
    private List<Long> contractIds;
    @ApiModelProperty(value = "合同编号")
    private List<String> contractCodes;
    @ApiModelProperty(value = "被告列表")
    private List<LitigationDefendantDto> defendants;
    @ApiModelProperty(value = "进展列表")
    private List<LitigationCaseProgressDto> caseProgresses;
    @ApiModelProperty(value = "诉讼登记信息")
    private LitigationTrialInfoDto trialInfo;
}
