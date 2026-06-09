package cn.zswltech.mithras.rating.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AreaModelIndexEnum implements PullDown {

    /**
     * 区域模型指标编号指标名称映射
     */
    gp_budget_revenue("一般公共预算收入(亿元)","gpBudgetRevenue","亿元"),
    gdp("GDP(亿元)","gdp","亿元"),
    secondary_industry_rt("GDP：第二产业占比(%)","secondaryIndustryRt","%"),
    town_pcdi("城镇居民人均可支配收入（单位：元）","townPcdi","元"),
    resident_pop_yoy_ratio("人口同比变化(%)","residentPopYoyRatio","%"),
    house_avg_price_sc("房屋平均单价(元/㎡) （年末，二手房）","houseAvgPriceSc","元/㎡"),
    government_fund_income("政府性基金收入(亿元)","governmentFundIncome","亿元"),
    tax_income_ratio("税收收入占比(%)","taxIncomeRatio","%"),
    budget_balance("财政平衡性","budgetBalance",""),
    debt_ratio("负债率","debtRatio","%"),
    debt_rate("债务率","debtRate","%"),
    debt_large_rt("广义城投债务倍数","debtLargeRt",""),
    government_transparency("政府透明度(%)","governmentTransparency","%"),
    house_avg_price_new("房屋平均单价(元/㎡) （年末，新房）","houseAvgPriceNew","元/㎡"),
    comprehensive_gdp_rt("财政收入占GDP比例","comprehensiveGdpRt",""),
//    city_score("所属地级市评分","cityScore",""),
//    class_adj("区域分类调整指标","classAdj",""),
    secotertiary_industry_rt("GDP：二三产业占比(%)","secotertiaryIndustryRt","%"),
    ;

    public final String display;
    public final String fieldName;
    public final String unit;

    @Override
    public String display() {
        return display;
    }

    public static AreaModelIndexEnum findByFieldName(String fieldName) {
        for (AreaModelIndexEnum item : values()) {
            if (item.getFieldName().equals(fieldName)) {
                return item;
            }
        }
        return null;
    }

    public static AreaModelIndexEnum find(String name) {
        for (AreaModelIndexEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
