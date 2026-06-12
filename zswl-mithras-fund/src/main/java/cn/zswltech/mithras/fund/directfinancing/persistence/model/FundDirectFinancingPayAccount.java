package cn.zswltech.mithras.fund.directfinancing.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 直接融资-还款账户
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
public class FundDirectFinancingPayAccount extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 融资id
    */
    @TableField("financing_id")
    private Long financingId;

    /**
    * 基础数据-我方银行账户id
    */
    @TableField("bank_account_id")
    private Long bankAccountId;

    /**
    * 支行名称
    */
    @TableField("account_bank")
    private String accountBank;

    /**
    * 账号
    */
    @TableField("account_number")
    private String accountNumber;

    /**
    * 账户类型
    */
    @TableField("account_type")
    private String accountType;

    /**
     * 开户时间
     */
    @TableField("account_opening_date")
    private LocalDate accountOpeningDate;

}
