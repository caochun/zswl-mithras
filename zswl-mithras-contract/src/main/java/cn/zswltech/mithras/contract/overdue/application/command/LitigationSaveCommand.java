package cn.zswltech.mithras.contract.overdue.application.command;

import cn.zswltech.mithras.contract.overdue.application.dto.LitigationTrialInfoDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 11:47
 */
@Data
public class LitigationSaveCommand {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "合同id")
    private List<Long> contractIds;

    @ApiModelProperty(value = "合同code")
    private List<String> contractCodes;

    @ApiModelProperty(value = "审判信息")
    private LitigationTrialInfoDto trialInfo;

}
