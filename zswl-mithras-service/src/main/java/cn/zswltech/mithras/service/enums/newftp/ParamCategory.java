package cn.zswltech.mithras.service.enums.newftp;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description: ftp 基础数据参数类别枚举
 * @author: zhaozhengkang
 * @date: 2023/5/18 13:54
 */
public enum ParamCategory implements PullDown {
    /**
     * 金融市场波动计价权重
     */
    FINANCIAL_MARKET_VOLATILITY("金融市场波动计价权重"),
    /**
     * 资产行业计价标准
     */
    INDUSTRY_ASSET_VALUATION("资产行业计价标准"),
    /**
     * 产业类客户主体计价标准
     */
    INDUSTRY_CUSTOMER_VALUATION("产业类客户主体计价标准"),
    /**
     * 地区分类计价标准
     */
    REGIONAL_CLASSIFICATION_VALUATION("地区分类计价标准"),
    /**
     * 10年期国债收益率波动水平计价标准
     */
    //TEN_YEAR_TREASURY_BOND_YIELD_VOLATILITY("10年期国债收益率波动水平计价标准"),
    /**
     * 成本趋势波动水平标准， 20240328版本删除
     */
    //COST_TREND_VOLATILITY("成本趋势波动水平标准"),
    /**
     * 地区分类最低综合补偿率， 20240328版本删除
     */
    //REGIONAL_CLASSIFICATION_MINIMUM_COMPENSATION_RATE("地区分类最低综合补偿率"),
    /**
     * 客户主体最低综合补偿率， 20240328版本删除
     */
    //CUSTOMER_SUBJECT_MINIMUM_COMPENSATION_RATE("客户主体最低综合补偿率"),

    /**
     * 最低综合补偿率， 20240328版本新增
     */
    MINIMUM_COMPREHENSIVE_COMPENSATION_RATE("最低综合补偿率"),

    /**
     * 金融市场波动计价标准， 20240328版本新增
     */
    VALUATION_STANDARDS_FOR_FINANCIAL_MARKET_VOLATILITY("金融市场波动计价标准"),

    /**
     * 协同最低收益率
     */
    COLLABORATIVE_MINIMUM_RATE("协同最低收益率")
    ;

    private final String display;

    ParamCategory(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return this.display;
    }
}
