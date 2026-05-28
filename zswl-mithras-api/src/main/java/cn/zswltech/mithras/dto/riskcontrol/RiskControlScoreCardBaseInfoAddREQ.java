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
@ApiModel("评分卡基本信息-新增-请求体")
public class RiskControlScoreCardBaseInfoAddREQ {

    /**
    * 评分卡名称
    */
    @ApiModelProperty(value = "评分卡名称")
    @NotNull(message = "评分卡名称不能为空")
    private String scorecardName;

    /**
    * 适用行业
    */
    @ApiModelProperty(value = "适用行业 RiskControlIndustryClassify")
    @NotNull(message = "适用行业不能为空")
    private String suitTrade;

    /**
    * 省内省外
    */
    @ApiModelProperty(value = "省内省外 ProvinceTypeEnum")
    @NotNull(message = "省内省外不能为空")
    private String provinceSeat;

    /**
    * 年份
    */
    @ApiModelProperty(value = "年份")
    @NotNull(message = "年份不能为空")
    private Integer year;

    /**
    * 说明
    */
    @ApiModelProperty(value = "说明")
    private String content;

    /**
     * 状态，0禁用，1启用
     **/
    @ApiModelProperty("状态，0禁用，1启用 RiskControlAssertEnum")
    private String status;


}
