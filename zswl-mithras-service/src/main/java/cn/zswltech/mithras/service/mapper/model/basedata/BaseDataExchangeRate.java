package cn.zswltech.mithras.service.mapper.model.basedata;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/9/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("base_data_exchange_rate")
public class BaseDataExchangeRate extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 年份
     */
    @TableField(value = "target_year")
    private Integer targetYear;

    /**
     * 月份
     */
    @TableField(value = "target_month")
    private Integer targetMonth;

    /**
     * 汇率日期
     */
    @TableField(value = "target_date")
    private LocalDate targetDate;

    /**
     * 币种
     */
    @TableField(value = "currency")
    private String currency;

    /**
     * 汇率
     */
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    /**
     * 是否草稿数据
     */
    @TableField(value = "is_draft")
    private Integer isDraft;
}
