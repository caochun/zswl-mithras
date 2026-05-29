package cn.zswltech.mithras.service.mapper.model.riskcontrol;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description risk_control_score_card_area_and_target
 * @author vico
 * @date 2023-03-02
 */
@Data
public class RiskControlScoreCardAreaAndTarget extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 指标id
    */
    @TableField("target_id")
    private Long targetId;

    @TableId(value = "area")
    private String area;

    @TableId(value = "province")
    private String province;

    @TableId(value = "city")
    private String city;

    @TableId(value = "executive_level")
    private String executiveLevel;

    @TableId(value = "regional_level")
    private String regionalLevel;

    @TableField(value = "year")
    private Integer year;

    @TableField(value = "data")
    private String data;

    /**
    * 得分
    */
    @TableField("score")
    private String score;

}
