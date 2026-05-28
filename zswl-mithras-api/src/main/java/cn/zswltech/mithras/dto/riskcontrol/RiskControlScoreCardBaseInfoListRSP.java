package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description risk_control_score_cade_base_info
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("评分卡基本信息-列表-返回体")
public class RiskControlScoreCardBaseInfoListRSP {

    /**
    * id
    */
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

    @ApiModelProperty("状态，0禁用，1启用 RiskControlAssertEnum")
    private String status;

    /**
    * 说明
    */
    @ApiModelProperty(value = "说明")
    private String content;

    @ApiModelProperty(value = "createTime")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "create_by")
    private Long createBy;

    @ApiModelProperty(value = "update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty("update_by")
    private Long updateBy;
}
