package cn.zswltech.mithras.dto.riskcontrol;

import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordAreaType;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordOptionGrade;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description risk_control_score_card_target
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("评分卡指标-新增-请求体")
public class RiskControlScoreCardTargetAddREQ {

    @NotNull(message = "评分卡id不能为空")
    @ApiModelProperty(value = "评分卡id")
    private Long cardId;

    /**
    * 指标id
    */
    /*@ApiModelProperty(value = "指标id")
    @NotNull(message = "不能为空")
    private Long titleId;*/

    /**
    * 指标名称
    */
    @ApiModelProperty(value = "指标名称")
    @NotNull(message = "不能为空")
    private String targetName;

    /**
    * 指标权重
    */
    @ApiModelProperty(value = "指标权重")
    @NotNull(message = "不能为空")
    private Long targetWeight;

    /**
    * 打分类型
    */
    @ApiModelProperty(value = "打分类型 GradeEnum")
    @NotNull(message = "不能为空")
    private String gradeType;

    /**
    * 分区类型
    */
    @ApiModelProperty(value = "分区类型 AreaStatusEnum")
    private String areaStatus;

    @ApiModelProperty(value = "选项记分")
    private RiskControlScoreCordOptionGrade optionGrade;

    /**
    * 分区详情
    */
    @ApiModelProperty(value = "分区详情")
    private List<RiskControlScoreCordAreaType> areaConfig;


}
