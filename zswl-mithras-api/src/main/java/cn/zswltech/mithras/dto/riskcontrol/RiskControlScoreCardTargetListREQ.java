package cn.zswltech.mithras.dto.riskcontrol;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description risk_control_score_card_target
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("评分卡指标-列表-请求体")
public class RiskControlScoreCardTargetListREQ extends PageReq {
    /**
     * 指标id
     */
    @ApiModelProperty(value = "评分卡id")
    private Long cardId;
}
