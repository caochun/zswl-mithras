package cn.zswltech.mithras.report.mapper.base.model;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @description 征信报送-账户表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrAccountBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 借据编号
    */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
    * 最后一笔付款明细的实收日期
    */
    @TableField(value = "lending_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate lendingDate;

    /**
    * 业务类型
    */
    @TableField(value = "biz_type", updateStrategy = FieldStrategy.IGNORED)
    private String bizType;

    /**
    * 租金计算方式
    */
    @TableField(value = "rental_calc_type", updateStrategy = FieldStrategy.IGNORED)
    private String rentalCalcType;

    /**
    * 还款频率
    */
    @TableField(value = "repay_rate", updateStrategy = FieldStrategy.IGNORED)
    private String repayRate;

    /**
    * 保证金（单位：0.0001元）
    */
    @TableField(value = "earnest_money", updateStrategy = FieldStrategy.IGNORED)
    private Long earnestMoney;

    /**
    * 借款期限（单位：月）
    */
    @TableField(value = "proj_lease_month_count", updateStrategy = FieldStrategy.IGNORED)
    private Integer projLeaseMonthCount;

    /**
    * 借款金额（单位：0.0001元）
    */
    @TableField(value = "payment_amount", updateStrategy = FieldStrategy.IGNORED)
    private Long paymentAmount;

    /**
     * 客户编号
     */
    @TableField(value = "client_code")
    private String clientCode;

    /**
     * 客户名称
     */
    @TableField(value = "client_name")
    private String clientName;

    /**
     * 结清日期
     */
    @TableField(value = "closed_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate closedDate;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    /**
     * 到期日期
     */
    @TableField(value = "expiration_date")
    private LocalDate expirationDate;

    /**
     * 主客户id
     */
    @TableField(value = "client_id")
    private Long clientId;

    @Override
    public Set<String> ignoreCompareFieldNames() {
        Set<String> set = new HashSet<>();
        set.add("paymentId");
        set.add("clientId");
        set.add("clientName");
        set.addAll(super.ignoreCompareFieldNames());
        return set;
    }

    public String genBusinessKey(LocalDate unionDate) {
        return String.format("%s_%s_%s", paymentApplyCode, paymentId, LocalDateTimeUtil.format(unionDate, "yyyyMMdd"));
    }

}
