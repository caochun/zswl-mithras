package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.util.BigDecimalUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 月度计价指导编辑区表
 * @TableName new_ftp_monthly_deduction_draft
 */
@TableName(value ="new_ftp_monthly_deduction_draft")
@Data
public class NewFtpMonthlyDeductionDraft extends BaseModel implements IEntity, Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属指引id
     */
    @TableField(value = "ftp_id")
    private Long ftpId;

    /**
     * 期限
     */
    @TableField(value = "term_range")
    private String termRange;

    /**
     * 融资成本
     */
    @TableField(value = "financing_cost")
    private Integer financingCost;

    /**
     * 担保成本
     */
    @TableField(value = "guarantee_cost")
    private Integer guaranteeCost;

    /**
     * 成本费用计价-小计
     */
    @TableField(value = "subtotal_cost")
    private Integer subtotalCost;

    /**
     * 10年期国债收益率
     */
    @TableField(value = "treasury_bond_yield")
    private Integer treasuryBondYield;

    /**
     * 10年期国债收益率-权重
     */
    @TableField(value = "treasury_bond_yield_weight")
    private Integer treasuryBondYieldWeight;

    /**
     * 1年期shibor利率
     */
    @TableField(value = "shibor_rate")
    private Integer shiborRate;

    /**
     * 1年期shibor利率-权重
     */
    @TableField(value = "shibor_rate_weight")
    private Integer shiborRateWeight;

    /**
     * 同期LPR利率
     */
    @TableField(value = "lpr_rate")
    private Integer lprRate;

    /**
     * 同期LPR利率-权重
     */
    @TableField(value = "lpr_rate_weight")
    private Integer lprRateWeight;

    /**
     * 融资成本趋势
     */
    @TableField(value = "financing_cost_trends")
    private Integer financingCostTrends;

    /**
     * 融资成本趋势-权重
     */
    @TableField(value = "financing_cost_trends_weight")
    private Integer financingCostTrendsWeight;

    /**
     * 金融市场波动计价-小计
     */
    @TableField(value = "subtotal_rate")
    private Integer subtotalRate;

    /**
     * 资产行业计价-产业类-鼓励介入类
     */
    @TableField(value = "asset_encourage")
    private Integer assetEncourage;

    /**
     * 资产行业计价-产业类-适度支持类
     */
    @TableField(value = "asset_moderate")
    private Integer assetModerate;

    /**
     * 资产行业计价-产业类-谨慎支持类
     */
    @TableField(value = "asset_cautious")
    private Integer assetCautious;

    /**
     * 资产行业计价-公共事业类
     */
    @TableField(value = "asset_public")
    private Integer assetPublic;

    /**
     * 资产行业计价-民生消费类
     */
    @TableField(value = "asset_civil")
    private Integer assetCivil;

    /**
     * 资产行业计价-国有产业类
     */
    @TableField(value = "asset_state_owned")
    private Integer assetStateOwned;

    /**
     * 产业类-地区分类计价-浙江地区
     */
    @TableField(value = "industry_region_zhejiang")
    private Integer industryRegionZhejiang;

    /**
     * 产业类-地区分类计价-鼓励支持类地区（除浙江）
     */
    @TableField(value = "industry_region_encourage")
    private Integer industryRegionEncourage;

    /**
     * 产业类-地区分类计价-其他地区
     */
    @TableField(value = "industry_region_other")
    private Integer industryRegionOther;

    /**
     * 产业类-客户主体计价-上市公司
     * @deprecated V1版本中使用的老字段，只是为了兼容老版本数据展示，新版本中不再使用
     */
    @Deprecated
    @TableField(value = "customer_listed")
    private Integer customerListed;

    /**
     * 其他产业类-客户主体计价-上市公司/国有企业
     */
    @TableField(value = "customer_listed_state_owned")
    private Integer customerListedStateOwned;

    /**
     * 产业类-客户主体计价-国有企业
     * @deprecated V1版本中使用的老字段，只是为了兼容老版本数据展示，新版本中不再使用
     */
    @Deprecated
    @TableField(value = "customer_state_owned")
    private Integer customerStateOwned;

    /**
     * 其他产业类-客户主体计价-其他上市公司
     */
    @TableField(value = "customer_other_listed")
    private Integer customerOtherListed;

    /**
     * 其他产业类-客户主体计价-其他类
     */
    @TableField(value = "customer_other")
    private Integer customerOther;

    /**
     * 公共事业类（含民生消费类）-地区分类计价-浙江地区
     */
    @TableField(value = "public_utilities_region_zhejiang")
    private Integer publicUtilitiesRegionZhejiang;

    /**
     * 公共事业类（含民生消费类）-地区分类计价-鼓励支持类地区（除浙江）
     */
    @TableField(value = "public_utilities_region_encourage")
    private Integer publicUtilitiesRegionEncourage;

    /**
     * 公共事业类（含民生消费类）-地区分类计价-其他地区
     */
    @TableField(value = "public_utilities_region_other")
    private Integer publicUtilitiesRegionOther;

    /**
     * 民生消费类-地区分类计价-浙江地区
     */
    @TableField(value = "civil_consumption_region_zhejiang")
    private Integer civilConsumptionRegionZhejiang;

    /**
     * 民生消费类-地区分类计价-鼓励支持类地区
     */
    @TableField(value = "civil_consumption_region_encourage")
    private Integer civilConsumptionRegionEncourage;

    /**
     * 民生消费类-地区分类计价-其他地区
     */
    @TableField(value = "civil_consumption_region_other")
    private Integer civilConsumptionRegionOther;

    /**
     * 国有产业类-地区分类计价-浙江地区
     */
    @TableField(value = "state_owned_industry_region_zhejiang")
    private Integer stateOwnedIndustryRegionZhejiang;

    /**
     * 国有产业类-地区分类计价-鼓励支持类地区
     */
    @TableField(value = "state_owned_industry_region_encourage")
    private Integer stateOwnedIndustryRegionEncourage;

    /**
     * 国有产业类-地区分类计价-其他地区
     */
    @TableField(value = "state_owned_industry_region_other")
    private Integer stateOwnedIndustryRegionOther;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public void setMainId(Long id) {
        ftpId = id;
    }

    @Override
    public Long getMainId() {
        return ftpId;
    }

    public void subtotal() {
        this.subtotalCost = LongUtil.null2zero(this.getGuaranteeCost()) + LongUtil.null2zero(this.getFinancingCost());
        BigDecimal lpr = BigDecimalUtil.valueOf(this.getLprRateWeight()).multiply(BigDecimalUtil.valueOf(this.getLprRate()));
        BigDecimal shibor = BigDecimalUtil.valueOf(this.getShiborRateWeight()).multiply(BigDecimalUtil.valueOf(this.getShiborRate()));
        BigDecimal treasuryBond = BigDecimalUtil.valueOf(this.getTreasuryBondYieldWeight()).multiply(BigDecimalUtil.valueOf(this.getTreasuryBondYield()));
        BigDecimal financeTrends = BigDecimalUtil.valueOf(this.getFinancingCostTrendsWeight()).multiply(BigDecimalUtil.valueOf(this.getFinancingCostTrends()));
        BigDecimal subtotalRate = lpr.add(shibor).add(treasuryBond).add(financeTrends).divide(BigDecimalUtil.valueOf(1000000), 0, RoundingMode.HALF_UP);
        this.subtotalRate = subtotalRate.intValue();
    }
}