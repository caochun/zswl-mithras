package cn.zswltech.mithras.service.service.dashboard.guanyuandata.boss;

import cn.zswltech.mithras.service.service.dashboard.guanyuandata.GuanYuanColumnPopulate;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/5/20
 * @description
 */
@Data
public class AssetClassifyStatisticsDTO implements GuanYuanColumnPopulate {
    /**
     * 年份
     */
    private Integer year;
    /**
     * 季度
     */
    private Integer quarter;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 五级分类结果
     */
    private String classifyResult;
    /**
     * 风险敞口总和
     */
    private Long stockRiskExposureTotal;
    /**
     * 资产余额（亿元）总和
     */
    private Long assetBalanceTotal;

    @Override
    public void populate(Map<String, String> map) {
        this.setYear(Optional.ofNullable(map.get("year")).map(Integer::valueOf).orElse(null));
        this.setQuarter(Optional.ofNullable(map.get("quarter")).map(Integer::valueOf).orElse(null));
        this.setClassifyResult(Optional.ofNullable(map.get("classify_result")).orElse(null));
        this.setQuantity(Optional.ofNullable(map.get("quantity")).map(Integer::valueOf).orElse(null));
        this.setStockRiskExposureTotal(Optional.ofNullable(map.get("stock_risk_exposure_total")).map(Long::valueOf).orElse(null));
        this.setAssetBalanceTotal(Optional.ofNullable(map.get("asset_balance_total")).map(Long::valueOf).orElse(null));
    }
}
