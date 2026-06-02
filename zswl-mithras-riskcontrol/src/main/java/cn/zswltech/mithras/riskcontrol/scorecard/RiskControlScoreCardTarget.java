package cn.zswltech.mithras.riskcontrol.scorecard;

import cn.zswltech.mithras.riskcontrol.common.GradeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description risk_control_score_card_target
 * @author vico
 * @date 2023-02-27
 */
@Data
public class RiskControlScoreCardTarget extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *记分卡id
     **/
    @TableField("card_id")
    private Long cardId;


    /**
    * 指标名称
    */
    @TableField("target_name")
    private String targetName;

    /**
    * 指标权重
    */
    @TableField("target_weight")
    private Long targetWeight;

    /**
    * 打分类型
     * {@link GradeEnum#name()}
    */
    @TableField("grade_type")
    private String gradeType;

    /**
    * 分区类型
    */
    @TableField("area_status")
    private String areaStatus;

    /**
    * 分区详情
    */
    @TableField("area_config")
    private String areaConfig;

    /**
     * 选项记分详情
     **/
    @TableField("option_grade")
    private String optionGrade;

}
