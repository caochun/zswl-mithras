package cn.zswltech.mithras.report.mapper.base.model;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.time.LocalDate;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @description 征信报送-实际还款表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrActualRepayBase extends CrBaseModel implements Serializable {

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
    * 核销对应的实收日期
    */
    @TableField(value = "pay_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate payDate;

    /**
    * 实收金额（单位：0.0001元）
    */
    @TableField(value = "collection_amount", updateStrategy = FieldStrategy.IGNORED)
    private Long collectionAmount;

    /**
    * 实收本金（单位：0.0001元）
    */
    @TableField(value = "collection_principal", updateStrategy = FieldStrategy.IGNORED)
    private Long collectionPrincipal;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    public String genBusinessKey(Long operateRecordId, LocalDate collectionDate) {
        return String.format("%s_%s_%s", paymentApplyCode, operateRecordId, LocalDateTimeUtil.format(collectionDate, "yyyyMMdd"));
    }

}
