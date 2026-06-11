package cn.zswltech.mithras.fund.model.receiptrepay;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-对方收款账户
 * @date 2023-02-22
 */
@Data
public class FundReceiptAccount extends BaseModel implements Serializable, IEntity {

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
     * 客户名称
     */
    @TableField("client_name")
    @IncludeNull
    private String clientName;

    /**
     * 账户名称
     */
    @TableField("account_name")
    @IncludeNull
    private String accountName;

    /**
     * 银行账号
     */
    @TableField("account_num")
    @IncludeNull
    private String accountNum;

    /**
     * 开户行
     */
    @TableField("account_address")
    @IncludeNull
    private String accountAddress;

    @Override
    public void setMainId(Long id) {
        this.setReceiptRepayId(id);
    }

    @Override
    public Long getMainId() {
        return getReceiptRepayId();
    }

}
