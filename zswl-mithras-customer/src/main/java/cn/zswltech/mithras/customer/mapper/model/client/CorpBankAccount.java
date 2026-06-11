package cn.zswltech.mithras.customer.mapper.model.client;

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
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("corp_bank_account")
public class
CorpBankAccount extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否主账号
     */
    @TableField("main_account")
    @IncludeNull
    private Boolean mainAccount;

    /**
     * 账号名称
     */
    @TableField("account_name")
    private String accountName;

    /**
     * 银行账号
     */
    @TableField("account_number")
    private String accountNumber;

    /**
     * 开户行
     */
    @TableField("account_bank")
    private String accountBank;

    @TableField(value = "user_id")
    private Long userId;

}
