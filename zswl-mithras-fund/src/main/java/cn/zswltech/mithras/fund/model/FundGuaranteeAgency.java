package cn.zswltech.mithras.fund.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@Data
public class FundGuaranteeAgency extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 担保机构名称
     */
    @TableField("guarantee_agency_name")
    private String guaranteeAgencyName;

    /**
     * 担保机构编号
     */
    @TableField("guarantee_agency_code")
    private String guaranteeAgencyCode;

    /**
     * 统一社会信用代码
     */
    @TableField("usc_code")
    private String uscCode;

    /**
     * 是否集团内关联方 是/否
     */
    @TableField("related_party")
    private Integer relatedParty;

    /**
     * 成立日期
     */
    @TableField("establish_date")
    private LocalDate establishDate;

    /**
     * 核准日期
     */
    @TableField("approval_date")
    private LocalDate approvalDate;

    @TableField("long_time_license")
    private Boolean longTimeLicense;
    /**
     * 营业许可证到期日(如果许可证是非长期类型)
     */
    @TableField("biz_license_end_date")
    private LocalDate bizLicenseEndDate;

    /**
     * 经济类型
     */
    @TableField("economy_type")
    private String economyType;

    /**
     * 组织机构类型
     */
    @TableField("org_type")
    private String orgType;

    /**
     * 注册币种
     */
    @TableField("register_currency_type")
    private String registerCurrencyType;

    /**
     * 注册资本
     */
    @TableField("register_capital")
    private Long registerCapital;

    /**
     * 实收币种
     */
    @TableField("real_currency_type")
    private String realCurrencyType;

    /**
     * 实收资本
     */
    @TableField("real_capital")
    private Long realCapital;

    /**
     * 法人代表
     */
    @TableField("corp_represent")
    private String corpRepresent;

    @TableField("biz_scope")
    private String bizScope;

    @TableField("effect_total_limit")
    @IncludeNull
    private Long effectTotalLimit;

    @TableField("effect_date_from")
    @IncludeNull
    private LocalDate effectDateFrom;

    @TableField("effect_date_to")
    @IncludeNull
    private LocalDate effectDateTo;

    @TableField("abbreviation")
    private String abbreviation;

    @TableField("name_for_short")
    private String nameForShort;

    @TableField("remark")
    private String remark;


}
