package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 月度计价指导
 * @date 2023-05-21
 */
@Data
@ApiModel("月度计价指导列表-返回体")
public class NewFtpMonthlyDeductionListRSP extends ListBaseRSP {

    /**
     * 所属指引id
     */
    @ApiModelProperty(value = "所属指引id")
    private Long ftpId;

    /**
     * 期限
     */
    @ApiModelProperty(value = "期限")
    private String termRange;

    /**
    * 融资成本
    */
    @ApiModelProperty(value = "融资成本")
    private Integer financingCost;

    /**
    * 担保成本
    */
    @ApiModelProperty(value = "担保成本")
    private Integer guaranteeCost;

    /**
    * 成本费用计价-小计
    */
    @ApiModelProperty(value = "成本费用计价-小计")
    private Integer subtotalCost;

    /**
    * 10年期国债收益率
    */
    @ApiModelProperty(value = "10年期国债收益率")
    private Integer treasuryBondYield;

    /**
    * 10年期国债收益率-权重
    */
    @ApiModelProperty(value = "10年期国债收益率-权重")
    private Integer treasuryBondYieldWeight;

    /**
    * 1年期shibor利率
    */
    @ApiModelProperty(value = "1年期shibor利率")
    private Integer shiborRate;

    /**
    * 1年期shibor利率-权重
    */
    @ApiModelProperty(value = "1年期shibor利率-权重")
    private Integer shiborRateWeight;

    /**
    * 同期lpr利率
    */
    @ApiModelProperty(value = "同期lpr利率")
    private Integer lprRate;

    /**
    * 同期lpr利率-权重
    */
    @ApiModelProperty(value = "同期lpr利率-权重")
    private Integer lprRateWeight;

    /**
    * 融资成本趋势
    */
    @ApiModelProperty(value = "融资成本趋势")
    private Integer financingCostTrends;

    /**
    * 融资成本趋势-权重
    */
    @ApiModelProperty(value = "融资成本趋势-权重")
    private Integer financingCostTrendsWeight;

    /**
    * 金融市场波动计价-小计
    */
    @ApiModelProperty(value = "金融市场波动计价-小计")
    private Integer subtotalRate;

    /**
    * 资产行业计价-产业类-鼓励介入类
    */
    @ApiModelProperty(value = "资产行业计价-产业类-鼓励介入类")
    private Integer assetEncourage;

    /**
    * 资产行业计价-产业类-适度支持类
    */
    @ApiModelProperty(value = "资产行业计价-产业类-适度支持类")
    private Integer assetModerate;

    /**
    * 资产行业计价-产业类-谨慎支持类
    */
    @ApiModelProperty(value = "资产行业计价-产业类-谨慎支持类")
    private Integer assetCautious;

    /**
     * 资产行业计价-公共事业类
     */
    @ApiModelProperty(value = "资产行业计价-公共事业类")
    private Integer assetPublic;

    /**
     * 资产行业计价-民生消费类
     */
    @ApiModelProperty(value = "资产行业计价-民生消费类")
    private Integer assetCivil;

    /**
     * 资产行业计价-国有产业类
     */
    @ApiModelProperty(value = "资产行业计价-国有产业类")
    private Integer assetStateOwned;

    /**
    * 产业类-地区分类计价-浙江地区
    */
    @ApiModelProperty(value = "产业类-地区分类计价-浙江地区")
    private Integer industryRegionZhejiang;

    /**
    * 产业类-地区分类计价-鼓励支持类地区（除浙江）
    */
    @ApiModelProperty(value = "产业类-地区分类计价-鼓励支持类地区（除浙江）")
    private Integer industryRegionEncourage;

    /**
    * 产业类-地区分类计价-其他地区
    */
    @ApiModelProperty(value = "产业类-地区分类计价-其他地区")
    private Integer industryRegionOther;

    /**
     * 产业类-客户主体计价-上市公司
     * @deprecated V1版本中使用的老字段，只是为了兼容老版本数据展示，新版本中不再使用
     */
    @Deprecated
    @ApiModelProperty(value = "产业类-客户主体计价-上市公司")
    private Integer customerListed;

    /**
     * 产业类-客户主体计价-国有企业
     * @deprecated V1版本中使用的老字段，只是为了兼容老版本数据展示，新版本中不再使用
     */
    @Deprecated
    @ApiModelProperty(value = "产业类-客户主体计价-国有企业")
    private Integer customerStateOwned;

    /**
     * 其他产业类-客户主体计价-上市公司/国有企业
     */
    @ApiModelProperty(value = "其他产业类-客户主体计价-上市公司/国有企业")
    private Integer customerListedStateOwned;

    /**
     * 其他产业类-客户主体计价-其他上市公司
     */
    @ApiModelProperty(value = "其他产业类-客户主体计价-其他上市公司")
    private Integer customerOtherListed;

    /**
     * 产业类-客户主体计价-其他类
     */
    @ApiModelProperty(value = "产业类-客户主体计价-其他类")
    private Integer customerOther;

    /**
    * 公共事业类-地区分类计价-浙江地区
    */
    @ApiModelProperty(value = "公共事业类-地区分类计价-浙江地区")
    private Integer publicUtilitiesRegionZhejiang;

    /**
    * 公共事业类-地区分类计价-鼓励支持类地区（除浙江）
    */
    @ApiModelProperty(value = "公共事业类-地区分类计价-鼓励支持类地区（除浙江）")
    private Integer publicUtilitiesRegionEncourage;

    /**
    * 公共事业类-地区分类计价-其他地区
    */
    @ApiModelProperty(value = "公共事业类-地区分类计价-其他地区")
    private Integer publicUtilitiesRegionOther;

    /**
     * 民生消费类-地区分类计价-浙江地区
     */
    @ApiModelProperty(value = "民生消费类-地区分类计价-浙江地区")
    private Integer civilConsumptionRegionZhejiang;

    /**
     * 民生消费类-地区分类计价-鼓励支持类地区
     */
    @ApiModelProperty(value = "民生消费类-地区分类计价-鼓励支持类地区")
    private Integer civilConsumptionRegionEncourage;

    /**
     * 民生消费类-地区分类计价-其他地区
     */
    @ApiModelProperty(value = "民生消费类-地区分类计价-其他地区")
    private Integer civilConsumptionRegionOther;

    /**
     * 国有产业类-地区分类计价-浙江地区
     */
    @ApiModelProperty(value = "国有产业类-地区分类计价-浙江地区")
    private Integer stateOwnedIndustryRegionZhejiang;

    /**
     * 国有产业类-地区分类计价-鼓励支持类地区
     */
    @ApiModelProperty(value = "国有产业类-地区分类计价-鼓励支持类地区")
    private Integer stateOwnedIndustryRegionEncourage;

    /**
     * 国有产业类-地区分类计价-其他地区
     */
    @ApiModelProperty(value = "国有产业类-地区分类计价-其他地区")
    private Integer stateOwnedIndustryRegionOther;

}
