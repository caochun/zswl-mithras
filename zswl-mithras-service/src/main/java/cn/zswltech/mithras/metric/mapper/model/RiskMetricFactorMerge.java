package cn.zswltech.mithras.metric.mapper.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/9/21
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("risk_metric_factor_merge")
public class RiskMetricFactorMerge extends RiskMetricFactor {
    /**
     * 组织编码
     */
    @TableField("org_code")
    private String orgCode;

    /**
     * 币种
     */
    @TableField("factor_currency")
    private String factorCurrency;
}
