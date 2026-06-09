package cn.zswltech.mithras.rating.model;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/3/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("rating_client_area_indicator_config")
public class RatingClientAreaIndicatorConfig extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 类别编码 {@link CategoryCodeEnum#name()}
     */
    @TableField(value = "category_code")
    private String categoryCode;

    /**
     * 类别名称
     */
    @TableField(value = "category_name")
    private String categoryName;

    /**
     * 指标编码
     */
    @TableField(value = "indicator_code")
    private String indicatorCode;

    /**
     * 指标名称
     */
    @TableField(value = "indicator_name")
    private String indicatorName;

    /**
     * 指标单位
     */
    @TableField(value = "indicator_unit")
    private String indicatorUnit;

    /**
     * 指标数据类型
     */
    @TableField(value = "indicator_data_type")
    private String indicatorDataType;

    /**
     * 指标排序
     */
    @TableField(value = "indicator_sort")
    private Integer indicatorSort;

    @Getter
    public enum CategoryCodeEnum {
        // 政信主体区县级-定量指标
        zxztqxj_dlzb,
        // 政信主体地级市-定量指标
        zxztdjs_dlzb
    }

    @AllArgsConstructor
    @Getter
    public enum DmIndicatorCode {
        belong_city_score("所属地级市评分", ""),
        budget_balance("财政平衡性", ""),
        comprehensive_gdp_rt("财政收入占GDP比例", ""),
        comprehensive_resources("地方综合财力(亿元)", "亿元"),
        comprehensive_resources_rt("一般公共预算收入占比", "%"),
        debt_large_rt("广义城投债务倍数", ""),
        debt_rate("债务率", "%"),
        debt_ratio("负债率", "%"),
        fin_inst_cny_deposit_balance("金融机构各项存款余额(万元)", "万元"),
        fin_inst_cny_loan_balance("金融机构各项贷款余额(万元)", "万元"),
        gdp("GDP(亿元)", "亿元"),
        gdp_growth_rate("GDP增速", ""),
        goods_retail_sales_growth("社会消费品零售总额增速(单位:%)", "%"),
        government_fund_income("政府性基金收入(亿元)", "亿元"),
        government_transparency("政府透明度(%)", "%"),
        gov_income_all("地方财政总收入(亿元)", "亿元"),
        gov_income_all_gr("地方财政总收入增长率", "%"),
        gp_budget_revenue("一般公共预算收入(亿元)", "亿元"),
        gp_budget_revenue_gr("一般公共预算收入增长率", "%"),
        gp_budget_revenue_per("人均一般公共预算收入(元/人)", "元/人"),
        gp_budget_revenue_tx("一般公共预算收入增长弹性", ""),
        hide_debt_rate("地区城投有息债务合计/地区综合财力", ""),
        industrial_added_value("工业增加值(亿元)", "亿元"),
        land_sales_ratio("财政收入占GDP比例", "%"),
        local_gov_debt_balance("地方政府债务余额(万元)", "万元"),
        local_gov_debt_usage("地方债务限额使用率", "%"),
        nonstandard_debt_ratio("城投非标债务率", "%"),
        primary_industry_rt("GDP第一产业占比(%)", "%"),
        resident_pop_yoy_ratio("人口同比变化(%)", "%"),
        secondary_industry_rt("GDP第二产业占比(%)", "%"),
        secotertiary_industry_rt("GDP二三产业占比(%)", "%"),
        tax_income_ratio("税收收入占比(%)", "%"),
        tertiary_industry_rt("GDP第三产业占比(%)", "%"),
        town_pcdi("城镇居民人均可支配收入(元)", "元"),
        house_avg_price_new("房屋平均单价(元/㎡)(年末,新房)", "元/㎡"),
        house_avg_price_sc("房屋平均单价(元/㎡)(年末,二手房)", "元/㎡"),
        class_adj("区域分类调整指标", "");

        private final String display;
        private final String unit;
    }
}
