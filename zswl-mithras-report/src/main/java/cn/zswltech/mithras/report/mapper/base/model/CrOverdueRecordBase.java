package cn.zswltech.mithras.report.mapper.base.model;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.Data;

import java.time.LocalDate;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description 征信报送-逾期信息表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrOverdueRecordBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 生成日期 dealtime取日期
     */
    @TableField("gen_date")
    private LocalDate genDate;

    /**
     * 付款申请id
     */
    @TableField("payment_id")
    private Long paymentId;

    /**
    * 借据编号
    */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
    * 逾期本金（单位：0.0001元）
    */
    @TableField(value = "overdue_principal", updateStrategy = FieldStrategy.IGNORED)
    private Long overduePrincipal;

    /**
    * 逾期天数
    */
    @TableField(value = "overdue_day", updateStrategy = FieldStrategy.IGNORED)
    private Integer overdueDay;

    /**
    * 逾期总额（单位：0.0001元）
    */
    @TableField(value = "overdue_total", updateStrategy = FieldStrategy.IGNORED)
    private Long overdueTotal;

    /**
    * 逾期改变日期
    */
    @TableField(value = "overdue_change_date")
    private LocalDate overdueChangeDate;

    /**
     * 期项
     */
    @TableField(value = "phase")
    private String phase;

    public String genBusinessKey(LocalDate dealDate) {
        return String.format("%s_%s_%s", paymentApplyCode, paymentId, LocalDateTimeUtil.format(dealDate, "yyyyMMdd"));
    }

}
