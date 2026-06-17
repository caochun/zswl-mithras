package cn.zswltech.mithras.liquidity.persistence.model;

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
 * 回款账户配置表
 * </p>
 *
 * @author chenyifei
 * @since 2024-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fund_financing_account_setting")
public class FundFinancingAccountSetting extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 融资类型
     */
    @TableField("financing_type")
    private String financingType;

    /**
     * 账户基本表id
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 账户类别
     */
    @TableField("account_category")
    private String accountCategory;

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
     */
    @TableField("account_type")
    private String accountType;

    /**
     * 是否模拟结清
     */
    @TableField("simulate_settle")
    private Boolean simulateSettle;

    /**
     * 模拟结清日期
     */
    @TableField("settle_time")
    private LocalDate settleTime;

    /**
     * 模拟结清金额
     */
    @TableField("settle_amount")
    private Long settleAmount;

    /**
     * 原数据账户id
     */
    @TableField("account_origin_id")
    private Long accountOriginId;


}
