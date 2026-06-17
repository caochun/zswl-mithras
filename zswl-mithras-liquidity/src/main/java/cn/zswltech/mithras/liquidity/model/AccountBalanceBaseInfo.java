package cn.zswltech.mithras.liquidity.model;

import cn.zswltech.mithras.liquidity.enums.LiquidityBankAccountType;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 账户余额表
 * </p>
 *
 * @author chenyifei
 * @since 2024-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("account_balance_base_info")
public class AccountBalanceBaseInfo extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据时点
     */
    @TableField("date")
    private LocalDate date;

    /**
     * 账户基本表id
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 开户银行
     */
    @TableField("account_bank")
    private String accountBank;

    /**
     * 银行账号
     */
    @TableField("account_number")
    private String accountNumber;

    /**
     * 账户性质
     * {@link LiquidityBankAccountType#name()}
     */
    @TableField("account_type")
    private String accountType;

    /**
     * 提款(编辑字段)
     */
    @TableField("drawings_amount")
    private Long drawingsAmount;

    /**
     * 租金回流
     */
    @TableField("rent_reflow_amount")
    private Long rentReflowAmount;

    /**
     * 其他流入(编辑字段)
     */
    @TableField("other_flow_amount")
    private Long otherFlowAmount;

    /**
     * 投放
     */
    @TableField("payment_amount")
    private Long paymentAmount;

    /**
     * 还本付息
     */
    @TableField("repay_amount")
    private Long repayAmount;

    /**
     * 还本付息-调整(编辑字段)
     */
    @TableField("repay_edit_amount")
    private Long repayEditAmount;

    /**
     * 还本付息-abs
     */
    @TableField("repay_abs_amount")
    private Long repayAbsAmount;

    /**
     * 还本付息-非abs
     */
    @TableField("repay_no_abs_amount")
    private Long repayNoAbsAmount;

    /**
     * 刚性支出(编辑字段)
     */
    @TableField("must_expense_amount")
    private Long mustExpenseAmount;

    /**
     * 其他支出(编辑字段)
     */
    @TableField("other_expense_amount")
    private Long otherExpenseAmount;

    /**
     * 结余-预估
     */
    @TableField("estimate_balance_amount")
    private Long estimateBalanceAmount;

    /**
     * 结余受限-预估(系统取值)
     */
    @TableField("estimate_balance_limit_amount")
    private Long estimateBalanceLimitAmount;

    /**
     * 结余受限-预估(编辑字段)   结余受限-若编辑字段有值则取编辑字段的值
     */
    @TableField("estimate_balance_limit_edit_amount")
    private Long estimateBalanceLimitEditAmount;

    /**
     * 结余-实际(编辑字段)
     */
    @TableField("actual_balance_amount")
    private Long actualBalanceAmount;

    /**
     * 差额
     */
    @TableField("diff_amount")
    private Long diffAmount;


}
