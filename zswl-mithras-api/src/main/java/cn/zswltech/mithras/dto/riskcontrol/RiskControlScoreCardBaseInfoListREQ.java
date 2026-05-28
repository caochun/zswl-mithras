package cn.zswltech.mithras.dto.riskcontrol;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description risk_control_score_cade_base_info
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("评分卡基本信息-列表-请求体")
public class RiskControlScoreCardBaseInfoListREQ extends PageReq {

    /**
     * 评分卡名称
     */
    @ApiModelProperty(value = "评分卡名称")
    private String scorecardName;

    /**
     * 适用行业
     */
    @ApiModelProperty(value = "适用行业")
    private String suitTrade;

    /**
     * 状态，0禁用，1启用
     **/
    @ApiModelProperty("状态，0禁用，1启用 RiskControlAssertEnum")
    private String status;

    @ApiModelProperty("更新开始时间")
    private LocalDate updateTimeFrom;

    @ApiModelProperty("更新结束时间")
    private LocalDate updateTimeTo;

}
