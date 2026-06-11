package cn.zswltech.mithras.fund.mapper.model.financing;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_lpr_adjust_record")
public class FundLprAdjustRecord extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 融资申请id
     */
    @TableField("financing_id")
    private Long financingId;
    /**
     * 调整前lpr利率
     */
    @TableField("before_lpr_rate_percent")
    private Integer beforeLprRatePercent;

    /**
     * 调整后lpr利率
     */
    @TableField("after_lpr_rate_percent")
    private Integer afterLprRatePercent;
    /**
     * 调整后lpr利率
     */
    @TableField("lpr_date")
    private LocalDate lprDate;


    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}
