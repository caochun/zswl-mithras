package cn.zswltech.mithras.riskcontrol.scorecard;

import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordOptionGrade;
import lombok.Data;

/**
 * @ClassName RiskControlScoreCardAreaAndTargetDto
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/3 10:33 上午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCardAreaAndTargetDto {

    /**
     * 指标id
     */
    private Long targetId;

    /**
     * 指标名称
     */
    private String targetName;

    /**
     * 指标权重
     */
    private Long targetWeight;

    /**
     * 打分类型
     */
    private String gradeType;

    /**
     *数值
     **/
    private String dataContent;

    /**
     * 分区打分
     **/
    private RiskControlScoreCordOptionGrade optionGrade;

    /**
     * 得分
     **/
    private Long score;



}
