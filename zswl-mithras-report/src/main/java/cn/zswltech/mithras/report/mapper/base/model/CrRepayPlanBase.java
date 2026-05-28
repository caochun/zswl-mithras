package cn.zswltech.mithras.report.mapper.base.model;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 征信报送-还款计划表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrRepayPlanBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 借据编号
    */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
    * 期项
    */
    @TableField(value = "phase")
    private Integer phase;

    /**
    * 日期
    */
    @TableField(value = "cash_flow_date")
    private LocalDate cashFlowDate;

    /**
    * 宽限期
    */
    @TableField(value = "grace_period", updateStrategy = FieldStrategy.IGNORED)
    private String gracePeriod;

    /**
    * 租金（单位：0.0001元）
    */
    @TableField(value = "rent", updateStrategy = FieldStrategy.IGNORED)
    private Long rent;

    /**
    * 本金（单位：0.0001元）
    */
    @TableField(value = "principal", updateStrategy = FieldStrategy.IGNORED)
    private Long principal;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    public String genBusinessKey() {
        return String.format("%s_%s_%s", paymentApplyCode, LocalDateTimeUtil.format(cashFlowDate, "yyyyMMdd"), phase);
    }

}
