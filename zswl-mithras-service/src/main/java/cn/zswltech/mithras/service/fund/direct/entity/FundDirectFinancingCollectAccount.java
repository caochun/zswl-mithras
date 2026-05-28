package cn.zswltech.mithras.service.fund.direct.entity;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.naming.Name;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 直接融资-对方收款账户
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
@TableName("fund_direct_financing_collect_account")
public class FundDirectFinancingCollectAccount extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

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


}
