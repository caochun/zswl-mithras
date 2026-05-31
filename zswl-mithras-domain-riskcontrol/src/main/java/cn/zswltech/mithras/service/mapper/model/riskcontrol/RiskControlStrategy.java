package cn.zswltech.mithras.service.mapper.model.riskcontrol;

import cn.zswltech.mithras.service.enums.riskcontrol.AlertState;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description 预警监控管理
 * @date 2023-02-09
 */
@Data
public class RiskControlStrategy implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 指标编号
     */
    @TableField("metric_code")
    private String metricCode;
    /**
     * 指标类型
     */
    @TableField("metric_type")
    private String metricType;

    /**
     * 指标类别
     */
    @TableField("metric_category")
    private String metricCategory;
    /**
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;
    /**
     * 计算逻辑
     */
    @TableField("computational_logic")
    private String computationalLogic;
    /**
     * 当前值1
     */
    @TableField("current_value_one")
    private Long currentValueOne;

    /**
     * 限定值1
     */
    @TableField(value = "limit_value_one", updateStrategy = FieldStrategy.IGNORED)
    private Long limitValueOne;
    /**
     * 预警值1
     */
    @TableField(value = "early_warning_value_one", updateStrategy = FieldStrategy.IGNORED)
    private Long earlyWarningValueOne;
    /**
     * 比较方式1
     */
    @TableField("comparison_method_one")
    private String comparisonMethodOne;
    /**
     * 单位1
     */
    @TableField("value_unit_one")
    private String valueUnitOne;

    private transient BigDecimal currentValueOneDecimal;
    /**
     * 当前值2
     */
    @TableField("current_value_two")
    private Long currentValueTwo;
    /**
     * 限定值2
     */
    @TableField(value = "limit_value_two", updateStrategy = FieldStrategy.IGNORED)
    private Long limitValueTwo;
    /**
     * 预警值2
     */
    @TableField(value = "early_warning_value_two", updateStrategy = FieldStrategy.IGNORED)
    private Long earlyWarningValueTwo;
    /**
     * 比较方式2
     */
    @TableField("comparison_method_two")
    private String comparisonMethodTwo;
    /**
     * 单位2
     */
    @TableField("value_unit_two")
    private String valueUnitTwo;

    private transient BigDecimal currentValueTwoDecimal;

    /**
     * 指标预警状态
     */
    @TableField("early_warning_state")
    private Integer earlyWarningState;

    @TableField("remaining_principal")
    private Long remainingPrincipal;

    /**
     * 速算上下文
     */
    @TableField("quick_context")
    private String quickContext;

    /**
     * 通用字段
     */
    @TableField("remark")
    private String remark;

    /**
     * 当前值为空的原因
     */
    @TableField(value = "null_reason", updateStrategy = FieldStrategy.IGNORED)
    private String nullReason;

    @TableField(value = "create_time")
    private LocalDateTime createTime;
    @TableField(value = "create_by")
    private Long createBy;
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
    @TableField("update_by")
    private Long updateBy;
    @TableField("client_detail")
    private String clientDetail;

    @TableField("last_early_waring_time")
    private LocalDate lastEarlyWaringTime;
    @TableField("last_limit_waring_time")
    private LocalDate lastLimitWaringTime;

    public boolean industryClassifyCanPass(Long thisAmount) {
        boolean result = true;
        if (currentValueOne != null) {
            BigDecimal leftOperand = new BigDecimal(currentValueOne)
                    .multiply(new BigDecimal(100000000L))
                    .add(new BigDecimal(null2zero(thisAmount)));
            BigDecimal rightOperand = new BigDecimal(limitValueOne)
                    .multiply(new BigDecimal(100000000L));
            if (">=".equals(comparisonMethodOne)) {
                result = leftOperand.compareTo(rightOperand) >= 0;
            }
            if ("<=".equals(comparisonMethodOne)) {
                result = leftOperand.compareTo(rightOperand) <= 0;
            }
        }
        if (currentValueTwo != null) {
            BigDecimal leftOperand = new BigDecimal(currentValueTwo)
                    .multiply(new BigDecimal(100000000L))
                    .add(new BigDecimal(null2zero(thisAmount)));
            BigDecimal rightOperand = new BigDecimal(limitValueTwo)
                    .multiply(new BigDecimal(100000000L));
            if (">=".equals(comparisonMethodTwo)) {
                result &= leftOperand.compareTo(rightOperand) >= 0;
            }
            if ("<=".equals(comparisonMethodTwo)) {
                result &= leftOperand.compareTo(rightOperand) <= 0;
            }
        }
        return result;
    }

    /**
     * 无需转换单位
     *
     * @return
     */
    public AlertState currentAlertState() {
        AlertState alertStateOne, alertStateTwo;
        if (limitValueOne == null) {
            return AlertState.NORMAL;
        }
        Long tmpEarlyWarningValueOne = earlyWarningValueOne == null ? limitValueOne : earlyWarningValueOne;
        if ("<=".equals(comparisonMethodOne)) {
            if (currentValueOne > limitValueOne) {
                alertStateOne = AlertState.OVER;
            } else if (currentValueOne > tmpEarlyWarningValueOne) {
                alertStateOne = AlertState.WARNING;
            } else {
                alertStateOne = AlertState.NORMAL;
            }
        } else if (">=".equals(comparisonMethodOne)) {
            if (currentValueOne < limitValueOne) {
                alertStateOne = AlertState.OVER;
            } else if (currentValueOne < tmpEarlyWarningValueOne) {
                alertStateOne = AlertState.WARNING;
            } else {
                alertStateOne = AlertState.NORMAL;
            }
        } else {
            alertStateOne = AlertState.NORMAL;
        }
        if (limitValueTwo == null || currentValueTwo == null) {
            return alertStateOne;
        }
        // 对比第二值
        Long tmpEarlyWarningValueTwo = earlyWarningValueTwo == null ? limitValueTwo : earlyWarningValueTwo;
        if ("<=".equals(comparisonMethodTwo)) {
            if (currentValueTwo > limitValueTwo) {
                alertStateTwo = AlertState.OVER;
            } else if (currentValueTwo > tmpEarlyWarningValueTwo) {
                alertStateTwo = AlertState.WARNING;
            } else {
                alertStateTwo = AlertState.NORMAL;
            }
        } else if (">=".equals(comparisonMethodTwo)) {
            if (currentValueTwo < limitValueTwo) {
                alertStateTwo = AlertState.OVER;
            } else if (currentValueTwo < tmpEarlyWarningValueTwo) {
                alertStateTwo = AlertState.WARNING;
            } else {
                alertStateTwo = AlertState.NORMAL;
            }
        } else {
            alertStateTwo = AlertState.NORMAL;
        }
        // 综合
        if (alertStateOne == AlertState.OVER || alertStateTwo == AlertState.OVER) {
            return AlertState.OVER;
        } else if (alertStateOne == AlertState.WARNING || alertStateTwo == AlertState.WARNING) {
            return AlertState.WARNING;
        } else {
            return AlertState.NORMAL;
        }
    }

    public AlertState currentAlertState(Long currentValueOne, Long currentValueTwo) {
        if (limitValueOne == null) {
            return AlertState.NORMAL;
        }
        AlertState alertStateOne, alertStateTwo;
        // 对比第一值
        Long tmpLimitValueOne = getTmpLimitValue(limitValueOne, valueUnitOne);
        Long tmpEarlyWarningValueOne = getTmpEarlyWaringValue(earlyWarningValueOne, limitValueOne, valueUnitOne);
        if ("<=".equals(comparisonMethodOne)) {
            if (currentValueOne > tmpLimitValueOne) {
                alertStateOne = AlertState.OVER;
            } else if (currentValueOne > tmpEarlyWarningValueOne) {
                alertStateOne = AlertState.WARNING;
            } else {
                alertStateOne = AlertState.NORMAL;
            }
        } else if (">=".equals(comparisonMethodOne)) {
            if (currentValueOne < tmpLimitValueOne) {
                alertStateOne = AlertState.OVER;
            } else if (currentValueOne < tmpEarlyWarningValueOne) {
                alertStateOne = AlertState.WARNING;
            } else {
                alertStateOne = AlertState.NORMAL;
            }
        } else {
            alertStateOne = AlertState.NORMAL;
        }
        if (limitValueTwo == null || currentValueTwo == null) {
            return alertStateOne;
        }
        // 对比第二值
        Long tmpLimitValueTwo = getTmpLimitValue(limitValueTwo, valueUnitTwo);
        Long tmpEarlyWarningValueTwo = getTmpEarlyWaringValue(earlyWarningValueTwo, limitValueTwo, valueUnitTwo);
        if ("<=".equals(comparisonMethodTwo)) {
            if (currentValueTwo > tmpLimitValueTwo) {
                alertStateTwo = AlertState.OVER;
            } else if (currentValueTwo > tmpEarlyWarningValueTwo) {
                alertStateTwo = AlertState.WARNING;
            } else {
                alertStateTwo = AlertState.NORMAL;
            }
        } else if (">=".equals(comparisonMethodTwo)) {
            if (currentValueTwo < tmpLimitValueTwo) {
                alertStateTwo = AlertState.OVER;
            } else if (currentValueTwo < tmpEarlyWarningValueTwo) {
                alertStateTwo = AlertState.WARNING;
            } else {
                alertStateTwo = AlertState.NORMAL;
            }
        } else {
            alertStateTwo = AlertState.NORMAL;
        }
        // 综合
        if (alertStateOne == AlertState.OVER || alertStateTwo == AlertState.OVER) {
            return AlertState.OVER;
        } else if (alertStateOne == AlertState.WARNING || alertStateTwo == AlertState.WARNING) {
            return AlertState.WARNING;
        } else {
            return AlertState.NORMAL;
        }
    }

    private Long getTmpLimitValue(Long limitValue, String valueUnit) {
        if (limitValue == null) {
            return null;
        }
        if ("亿元".equals(valueUnit)) {
            return limitValue * 100000000L;
        } else {
            return limitValue;
        }
    }

    private Long getTmpEarlyWaringValue(Long earlyWarningValue, Long limitValue, String unit) {
        if (earlyWarningValue == null) {
            earlyWarningValue = limitValue;
        }
        if ("亿元".equals(unit)) {
            earlyWarningValue = earlyWarningValue * 100000000L;
        }
        return earlyWarningValue;
    }

    private Long null2zero(Long value) {
        return value == null ? 0L : value;
    }
}
