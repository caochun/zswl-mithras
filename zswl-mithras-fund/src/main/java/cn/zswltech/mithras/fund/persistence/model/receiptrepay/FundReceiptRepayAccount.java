package cn.zswltech.mithras.fund.persistence.model.receiptrepay;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 收付款账户信息
 * @date 2023-02-20
 */
@Data
public class FundReceiptRepayAccount extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收付款id
     */
    @TableField("receipt_repay_id")
    private Long receiptRepayId;

    /**
     * 账户类型（收款、还款）
     */
    @TableField("account_type")
    private String accountType;

    /**
     * 我方银行账户id
     */
    @TableField("bank_account_id")
    private Long bankAccountId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 账户名称
     */
    @TableField("account_name")
    private String accountName;

    /**
     * 银行账号
     */
    @TableField("bank_no")
    private String bankNo;

    /**
     * 开户行
     */
    @TableField("deposit_bank")
    private String depositBank;

    @Override
    public void setMainId(Long id) {
        this.setReceiptRepayId(id);
    }

    @Override
    public Long getMainId() {
        return this.getReceiptRepayId();
    }


}
