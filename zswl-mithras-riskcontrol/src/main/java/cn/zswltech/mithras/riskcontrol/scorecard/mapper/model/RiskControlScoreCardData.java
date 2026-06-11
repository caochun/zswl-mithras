package cn.zswltech.mithras.riskcontrol.scorecard.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
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
public class RiskControlScoreCardData extends BaseModel implements Serializable {

    /**
     * 方案id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title_name")
    private String titleName;

    @TableField("year")
    private Integer year;

    @TableField("content")
    private String content;

    @TableField("row")
    private Integer row;

}
