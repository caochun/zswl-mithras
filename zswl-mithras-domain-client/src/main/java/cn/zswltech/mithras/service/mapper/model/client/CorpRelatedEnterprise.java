package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("corp_related_enterprise")
public class CorpRelatedEnterprise extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联企业名称
     */
    @TableField("enterprise_name")
    private String enterpriseName;

    /**
     * 关联关系
     */
    @TableField("relationship")
    private String relationship;

    /**
     * 注册资本
     */
    @TableField("register_capital")
    @IncludeNull
    private Long registerCapital;

    /**
     * 持股比例
     */
    @TableField("shareholding_ratio")
    @IncludeNull
    private Long shareholdingRatio;

    /**
     * 投资金额（万元）
     */
    @TableField("invest_amount")
    @IncludeNull
    private Long investAmount;

    @TableField("continuous_status")
    private String continuousStatus;


    @TableField("establish_date")
    @IncludeNull
    private LocalDate establishDate;

    @TableField("industry_type")
    @IncludeNull
    private String industryType;

    @TableField(value = "user_id")
    private Long userId;

}
