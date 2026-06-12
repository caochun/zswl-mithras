package cn.zswltech.mithras.fund.persistence.model.financing;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_collect_account")
public class FundFinancingCollectAccount extends BaseModel implements IEntity {
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
     * 客户名称
     */
    @TableField(value = "client_name", updateStrategy = FieldStrategy.IGNORED)
    private String clientName;
    /**
     * 账户名称
     */
    @TableField(value = "account_name", updateStrategy = FieldStrategy.IGNORED)
    private String accountName;
    /**
     * 银行账号
     */
    @TableField(value = "account_num", updateStrategy = FieldStrategy.IGNORED)
    private String accountNum;
    /**
     * 开户行
     */
    @TableField(value = "account_address", updateStrategy = FieldStrategy.IGNORED)
    private String accountAddress;

    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}