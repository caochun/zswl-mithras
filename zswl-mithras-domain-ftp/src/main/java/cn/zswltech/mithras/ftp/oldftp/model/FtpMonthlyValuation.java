package cn.zswltech.mithras.ftp.oldftp.model;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description 月度计价指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
public class FtpMonthlyValuation extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属指引id
    */
    @TableField("guidance_id")
    private Long guidanceId;

    /**
    * 期限
    */
    @TableField("crdit_term")
    private String crditTerm;

    /**
    * 融资成本
    */
    @TableField("financing_cost")
    private Integer financingCost;

    /**
    * 担保成本
    */
    @TableField("guarantee_cost")
    private Integer guaranteeCost;

    /**
    * 成本小计
    */
    @TableField("subtotal_cost")
    private Integer subtotalCost;

    /**
    * 国股银票转贴现利率
    */
    @TableField("discount_rate")
    private Integer discountRate;

    /**
    * 权重1
    */
    @TableField("discount_rate_weight")
    private Integer discountRateWeight;

    /**
    * 1年期shibor利率
    */
    @TableField("shibor_rate")
    private Integer shiborRate;

    /**
    * 权重2
    */
    @TableField("shibor_rate_weight")
    private Integer shiborRateWeight;

    /**
    * 同期lpr利率
    */
    @TableField("lpr_rate")
    private Integer lprRate;

    /**
    * 权重3
    */
    @TableField("lpr_rate_weight")
    private Integer lprRateWeight;

    /**
    * 融资成本趋势
    */
    @TableField("finance_cost_trends")
    private Integer financeCostTrends;

    /**
    * 权重4
    */
    @TableField("finance_cost_trends_weight")
    private Integer financeCostTrendsWeight;

    /**
    * 小计
    */
    @TableField("subtotal_rate")
    private Integer subtotalRate;

    /**
    * 调整后计价小计
    */
    @TableField("subtotal_adjustment_valuation")
    private Integer subtotalAdjustmentValuation;

    /**
    * 鼓励介入类
    */
    @TableField("encourage_valuation")
    private Integer encourageValuation;

    /**
    * 适度类
    */
    @TableField("moderate_support_valuation")
    private Integer moderateSupportValuation;

    /**
    * 谨慎支持类
    */
    @TableField("cautious_valuation")
    private Integer cautiousValuation;

    /**
    * 国有/上市公司
    */
    @TableField("state_own_listed_valuation")
    private Integer stateOwnListedValuation;

    /**
    * 其他类
    */
    @TableField("other_valuation")
    private Integer otherValuation;

    @Override
    public void setMainId(Long id) {
        this.guidanceId = id;
    }

    @Override
    public Long getMainId() {
        return this.guidanceId;
    }
}
