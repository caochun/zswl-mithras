package cn.zswltech.mithras.report.mapper.base.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.time.LocalDate;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @author wang
 * @description 征信报送-特定交易表
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrSpecialTradeBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 借据编号
     */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
     * 交易类型
     */
    @TableField(value = "trade_type")
    private String tradeType;

    /**
     * 交易日期
     */
    @TableField(value = "trade_date")
    private LocalDate tradeDate;

    /**
     * 交易金额（单位：0.0001元）
     */
    @TableField(value = "trade_amount")
    private Long tradeAmount;

    /**
     * 到期日变更月数
     */
    @TableField(value = "change_month_count")
    private Integer changeMonthCount;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    public String genBusinessKey(Long specialTradeId) {
        return String.format("%s-%s", paymentApplyCode, specialTradeId);
    }

}
