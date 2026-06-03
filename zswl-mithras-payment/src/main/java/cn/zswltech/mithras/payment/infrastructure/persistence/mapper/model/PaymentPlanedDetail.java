package cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description 计划付款明细表（付款申请 1:n付款明细）
 * @author zhaozhengkang
 * @date 2022-08-12
 */
@Data
public class PaymentPlanedDetail extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属支付id
    */
    @TableField("payment_id")
    private Long paymentId;

    /**
    * 收款方客户id
    */
    @TableField("payee_client_id")
    private Long payeeClientId;

    /**
    * 收款方客户名称
    */
    @TableField("payee_client_name")
    private String payeeClientName;

    /**
    * 对方账号
    */
    @TableField("opposite_account")
    private String oppositeAccount;

    /**
    * 对方账号名
    */
    @TableField("opposite_account_name")
    private String oppositeAccountName;

    /**
    * 对方账号开户行
    */
    @TableField("opposite_account_bank")
    private String oppositeAccountBank;

    /**
    * 支付方式
    */
    @TableField("payment_method")
    private String paymentMethod;

    /**
    * 支付金额
    */
    @TableField("payment_amount")
    private Long paymentAmount;

    /**
    * 附言
    */
    @TableField("postscript")
    @IncludeNull
    private String postscript;

    @Override
    public void setMainId(Long id) {
        setPaymentId(id);
    }

    @Override
    public Long getMainId() {
        return getPaymentId();
    }
}
