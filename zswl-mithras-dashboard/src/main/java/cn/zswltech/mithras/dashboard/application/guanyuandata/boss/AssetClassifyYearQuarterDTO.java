package cn.zswltech.mithras.dashboard.application.guanyuandata.boss;

import cn.zswltech.mithras.dashboard.application.guanyuandata.GuanYuanColumnPopulate;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/5/20
 * @description
 */
@Data
public class AssetClassifyYearQuarterDTO implements GuanYuanColumnPopulate {
    /**
     * 年份
     */
    private Integer year;
    /**
     * 季度
     */
    private Integer quarter;

    @Override
    public void populate(Map<String, String> map) {
        this.setYear(Optional.ofNullable(map.get("year")).map(Integer::valueOf).orElse(null));
        this.setQuarter(Optional.ofNullable(map.get("quarter")).map(Integer::valueOf).orElse(null));
    }
}
