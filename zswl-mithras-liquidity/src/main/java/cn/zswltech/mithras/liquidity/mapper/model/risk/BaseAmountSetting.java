package cn.zswltech.mithras.liquidity.mapper.model.risk;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @create: 2023-05-16
 * 基础数据设置
 * base_amount_setting
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "base_amount_setting")
@Data
public class BaseAmountSetting extends BaseModel implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 基础数据设置id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *期初现金流余额
     **/
    @TableField("begin_cashflow_amount")
    private Long beginCashflowAmount;

    /**
     *其他收入
     **/
    @TableField("other_income")
    private Long otherIncome;

    /**
     *其他支出
     **/
    @TableField("other_expenses")
    private Long otherExpenses;

}
