package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("normal_bank_account")
public class NormalBankAccount extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 开户行
     */
    @TableField("account_bank")
    private String accountBank;

    /**
     * 是否主账号
     */
    @TableField("main_account")
    @IncludeNull
    private Boolean mainAccount;


}
