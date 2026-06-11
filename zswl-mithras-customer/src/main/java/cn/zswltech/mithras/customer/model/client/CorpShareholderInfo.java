package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 股东信息
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("corp_shareholder_info")
public class CorpShareholderInfo extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 股东类型
     */
    @TableField("shareholder_type")
    private String shareholderType;

    /**
     * 股东姓名
     */
    @TableField("shareholder_name")
    private String shareholderName;

    /**
     * 认缴金额（万）
     */
    @TableField("paid_total")
    @IncludeNull
    private Long paidTotal;

    @TableField("actual_paid_total")
    @IncludeNull
    private Long actualPaidTotal;

    /**
     * 出资方式
     */
    @TableField("capital_way")
    @IncludeNull
    private String capitalWay;

    /**
     * 出资占比
     */
    @TableField("capital_percent")
    @IncludeNull
    private Long capitalPercent;

    /**
     * 是否实际控制人
     */
    @TableField("real_controller")
    @IncludeNull
    private Boolean realController;

    @TableField(value = "user_id")
    private Long userId;

}
