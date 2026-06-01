package cn.zswltech.mithras.service.mapper.model.riskcontrol;

import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlAssertEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description risk_control_score_cade_base_info
 * @author vico
 * @date 2023-02-27
 */
@Data
public class RiskControlScoreCardBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 评分卡名称
    */
    @TableField("scorecard_name")
    private String scorecardName;

    /**
    * 适用行业
    */
    @TableField("suit_trade")
    private String suitTrade;

    /**
    * 省内省外
    */
    @TableField("province_seat")
    private String provinceSeat;

    /**
    * 年份
    */
    @TableField("year")
    private Integer year;

    /**
    * 说明
    */
    @TableField("content")
    private String content;

    /**
     * 状态{@link RiskControlAssertEnum#name()}
     **/
    @TableField("status")
    private String status;

}
