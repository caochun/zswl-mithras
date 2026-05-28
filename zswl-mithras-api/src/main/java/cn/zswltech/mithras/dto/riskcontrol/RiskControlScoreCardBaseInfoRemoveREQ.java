package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description risk_control_score_cade_base_info
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("评分卡基本信息-删除-请求体")
public class RiskControlScoreCardBaseInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("ids")
    private List<Long> ids;

}
