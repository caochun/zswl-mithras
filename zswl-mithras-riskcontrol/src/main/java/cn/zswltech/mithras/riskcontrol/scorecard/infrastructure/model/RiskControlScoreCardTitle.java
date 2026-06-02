package cn.zswltech.mithras.riskcontrol.scorecard.infrastructure.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RiskControlScoreCardTitle
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 4:23 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCardTitle extends BaseModel implements Serializable {

    /**
     * 方案id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("year")
    private Integer year;

    @TableField("name")
    private String name;

    @TableField("column_number")
    private Integer columnNumber;

    //指标类型 0 指标， 1 其他
    @TableField("target_type")
    private Integer targetType;

}
