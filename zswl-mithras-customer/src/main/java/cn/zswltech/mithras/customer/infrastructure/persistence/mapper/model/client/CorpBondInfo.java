package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client;

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
@TableName("corp_bond_info")
public class CorpBondInfo extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评级日期
     */
    @TableField("rate_date")
    @IncludeNull
    private LocalDate rateDate;

    /**
     * 评级公司
     */
    @TableField("rate_company")
    @IncludeNull
    private String rateCompany;

    /**
     * 评级
     */
    @TableField("rate")
    @IncludeNull
    private String rate;

    /**
     * 评级展望
     */
    @TableField("rate_future")
    @IncludeNull
    private String rateFuture;

    /**
     * 发行总额，单位亿元
     */
    @TableField("issue_total")
    @IncludeNull
    private Long issueTotal;

    /**
     * 发行只数
     */
    @TableField("issue_amount")
    @IncludeNull
    private Long issueAmount;

    /**
     * 存量规模，单位：亿元
     */
    @TableField("stock_scale")
    @IncludeNull
    private Long stockScale;

    /**
     * 存量只数
     */
    @TableField("stock_amount")
    @IncludeNull
    private Long stockAmount;

    /**
     * 到期规模，单位：亿元
     */
    @TableField("maturity_scale")
    @IncludeNull
    private Long maturityScale;

    /**
     * 到期只数
     */
    @TableField("maturity_amount")
    @IncludeNull
    private Long maturityAmount;

    @TableField(value = "user_id")
    private Long userId;

}
