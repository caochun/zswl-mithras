package cn.zswltech.mithras.assetclassify.model;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("asset_classify")
public class AssetClassify extends BaseModel implements IEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    @TableField("year")
    private Integer year;

    /**
     * 季度
     */
    @TableField("quarter")
    private Integer quarter;

    /**
     *各级分类具体数据
     **/
    @TableField("classify_amount")
    @Deprecated
    private String classifyAmount;

    /**
     * 是否结束 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("finish")
    private Integer finish;

    /**
     * 初分类型: QUARTER_END 季末初分, QUARTER_MID 季中初分
     */
    @TableField("init_type")
    private String initType;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
