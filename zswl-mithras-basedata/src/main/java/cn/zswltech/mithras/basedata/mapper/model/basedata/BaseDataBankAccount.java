package cn.zswltech.mithras.basedata.mapper.model;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountStatusEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description 基础数据-我方账户
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("base_data_bank_account")
public class BaseDataBankAccount extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 账户类型 {@link BaseDataBankAccountTypeEnum#name()}
     */
    @TableField("account_type")
    private String accountType;

    /**
     * 账户名称
     */
    @TableField("account_name")
    private String accountName;

    /**
     * 账号
     */
    @TableField("account_number")
    private String accountNumber;

    /**
     * 支行名称
     */
    @TableField("account_bank")
    private String accountBank;

    /**
     * 是否贷款账户 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("is_loan")
    private Integer isLoan;

    /**
     * 账户状态 {@link BaseDataBankAccountStatusEnum#name()}
     */
    @TableField("account_status")
    private String accountStatus;

    /**
     * 开户时间
     */
    @IncludeNull
    @TableField("opening_date")
    private LocalDate openingDate;

    /**
     * 币种
     */
    @IncludeNull
    @TableField("currency")
    private String currency;

    /**
     * 账户余额
     */
    @IncludeNull
    @TableField("account_balance")
    private Long accountBalance;

    /**
     * 备注
     */
    @IncludeNull
    @TableField("remark")
    private String remark;
}
