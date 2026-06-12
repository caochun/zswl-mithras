package cn.zswltech.mithras.fund.persistence.model.receiptrepay;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-我方付款账户
 * @date 2023-02-22
 */
@Data
public class FundRepayAccount extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 融资id
     */
    @TableField("receipt_repay_id")
    private Long receiptRepayId;

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

    @Override
    public void setMainId(Long id) {
        this.setReceiptRepayId(id);
    }

    @Override
    public Long getMainId() {
        return getReceiptRepayId();
    }

}
