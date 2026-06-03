package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing;

import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_pay_account")
public class FundFinancingPayAccount extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
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
     * 账户类型 {@link BaseDataBankAccountTypeEnum#name()}
     */
    @TableField("account_type")
    private String accountType;

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
     * 开户时间
     */
    @TableField("account_opening_date")
    private LocalDate accountOpeningDate;

    /**
     * 账户类别 {@link FundFinancingAccountTypeEnum#name()}
     */
    @TableField("account_category")
    private String accountCategory;


    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}