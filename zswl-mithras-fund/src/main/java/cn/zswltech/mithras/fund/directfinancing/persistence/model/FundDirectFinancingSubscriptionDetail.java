package cn.zswltech.mithras.fund.directfinancing.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 直接融资-认购明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
public class FundDirectFinancingSubscriptionDetail extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("financing_id")
    private Long financingId;

    /**
     * 认购机构
     */
    @TableField("orgnization_id")
    private Long orgnizationId;

    @TableField("orgnization_name")
    private String orgnizationName;

    /**
     * 认购证券
     */
    @TableField("product_id")
    private Long productId;

    @TableField("product_name")
    private String productName;

    /**
     * 认购额度（万元）
     */
    @TableField("subscription_limit")
    private Long subscriptionLimit;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    private transient String isCreditOrg;

}
