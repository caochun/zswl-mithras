package cn.zswltech.mithras.dto.riskcontrol;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description risk_control_score_cade_base_info
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("评分卡基本信息-编辑-请求体")
public class RiskControlScoreCardBaseInfoModifyREQ {

    /**
    * id
    */
    @NotNull(message = "不能为空")
    @ApiModelProperty(value = "id")
    private Long id;

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
    * 省内省外
    */
    @ApiModelProperty(value = "省内省外")
    private String provinceSeat;

    /**
    * 年份
    */
    @ApiModelProperty(value = "年份")
    private Integer year;

    /**
    * 说明
    */
    @ApiModelProperty(value = "说明")
    private String content;

    @ApiModelProperty("状态，0禁用，1启用 RiskControlAssertEnum")
    private String status;

}
