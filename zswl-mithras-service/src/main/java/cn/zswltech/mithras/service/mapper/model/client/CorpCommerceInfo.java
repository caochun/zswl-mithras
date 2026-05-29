package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.common.annotation.IncludeNull;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 法人工商信息表
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("corp_commerce_info")
public class CorpCommerceInfo extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 三证合一
     */
    @TableField("triple_cert_in_one")
    private Boolean tripleCertInOne;

    /**
     * 境内or境外
     */
    @TableField("domestic_or_abroad")
    private String domesticOrAbroad;

    /**
     * 特殊机构代码
     */
    @TableField(value = "special_org_code")
    @IncludeNull
    private String specialOrgCode;

    /**
     * 中征码
     */
    @TableField(value = "zhong_zheng_code")
    @IncludeNull
    private String zhongZhengCode;

    /**
     * 组织机构代码
     */
    @TableField(value = "org_code")
    @IncludeNull
    private String orgCode;

    /**
     * 营业执照号
     */
    @TableField(value = "biz_license_code")
    @IncludeNull
    private String bizLicenseCode;

    /**
     * 存续状态
     */
    @TableField(value = "continuous_status")
    @IncludeNull
    private String continuousStatus;

    /**
     * 成立日期
     */
    @TableField(value = "establish_date")
    @IncludeNull
    private LocalDate establishDate;

    /**
     * 核准日期
     */
    @TableField(value = "approval_date")
    @IncludeNull
    private LocalDate approvalDate;

    /**
     * 营业许可证是否为长期
     */
    @TableField(value = "biz_licence_long_term")
    @IncludeNull
    private Boolean bizLicenceLongTerm;

    /**
     * 营业许可证到期日(如果许可证是非长期类型)
     */
    @TableField(value = "biz_license_end_date")
    @IncludeNull
    private LocalDate bizLicenseEndDate;

    /**
     * 业务范围
     */
    @TableField(value = "biz_scope")
    @IncludeNull
    private String bizScope;

    /**
     * 行业分类
     */
    @TableField(value = "industry_type")
    @IncludeNull
    private String industryType;

    /**
     * 风控行业分类
     * {@link RiskControlIndustryClassify#name()}
     **/
    @TableField(value = "risk_control_industry_classify")
    private String riskControlIndustryClassify;


    /**
     * 经济类型
     */
    @TableField(value = "economy_type")
    @IncludeNull
    private String economyType;

    /**
     * 组织机构类型
     */
    @TableField(value = "org_type")
    @IncludeNull
    private String orgType;

    /**
     * 企业规模
     */
    @TableField(value = "org_scale")
    @IncludeNull
    private String orgScale;

    /**
     * 注册币种
     */
    @TableField(value = "register_currency_type")
    @IncludeNull
    private String registerCurrencyType;

    /**
     * 注册资本
     */
    @TableField(value = "register_capital")
    @IncludeNull
    private Long registerCapital;

    /**
     * 实收币种
     */
    @TableField(value = "real_currency_type")
    @IncludeNull
    private String realCurrencyType;

    /**
     * 实收资本
     */
    @TableField(value = "real_capital")
    @IncludeNull
    private Long realCapital;

    /**
     * 注册资本到位率
     */
    @TableField(value = "register_capital_rate")
    @IncludeNull
    private Long registerCapitalRate;

    /**
     * 法人代表
     */
    @TableField(value = "corp_represent")
    @IncludeNull
    private String corpRepresent;

    /**
     * 法人性别
     */
    @TableField(value = "corp_gender")
    @IncludeNull
    private String corpGender;

    /**
     * 法人证件类型
     */
    @TableField(value = "corp_cert_type")
    @IncludeNull
    private String corpCertType;

    /**
     * 法人证件号码
     */
    @TableField(value = "corp_cert_code")
    @IncludeNull
    private String corpCertCode;

    /**
     * 是否上市公司
     * 改成企业性质
     */
    @Deprecated
    @TableField(value = "listed_company")
    @IncludeNull
    private Boolean listedCompany;

    /**
     * 企业性质
     * {@link cn.zswltech.mithras.service.enums.client.EnterpriseNatureEnum}
     */
    @TableField(value = "enterprise_nature")
    @IncludeNull
    private String enterpriseNature;

    /**
     * 有上市公司控股
     */
    @TableField(value = "ownership_type")
    @IncludeNull
    private String ownershipType;

    /**
     * 客户编号
     */
    @TableField("client_code")
    @IncludeNull
    private String clientCode;

    /**
     * 是否关联方
     */
    @TableField("is_related")
    @IncludeNull
    private Integer isRelated;


    /**
     * 是否在沪深主板、中小板、创业板上市
     */
    @TableField("is_market")
    @IncludeNull
    @Deprecated
    private Integer isMarket;

    /**
     * 是否集团公司 1是，0否
     */
    @TableField("group_flag")
    @IncludeNull
    private Integer groupFlag;

    /**
     * 所属集团 法人客户id -1无 如果是自己的话也是传客户id
     */
    @TableField("belong_group_client_id")
    @IncludeNull
    private Long belongGroupClientId;

    //非数据库字段
    @TableField(exist = false)
    private String clientName;
    @TableField(exist = false)
    private String clientType;
    @TableField(exist = false)
    private String uscCode;

    /**
     * 所属集团客户名称
     */
    @TableField(exist = false)
    private String belongGroupClientName;

    @TableField(value = "user_id")
    private Long userId;

}
