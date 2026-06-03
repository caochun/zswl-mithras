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
public class AssetsIndustryDistributionDTO implements GuanYuanColumnPopulate {
    private String industryCode;
    private String industryDisplay;
    private Long balance;

    @Override
    public void populate(Map<String, String> map) {
        this.setIndustryCode(map.get("industry_code"));
        this.setIndustryDisplay(map.get("industry_display"));
        this.setBalance(Optional.ofNullable(map.get("balance")).map(Long::valueOf).orElse(0L));
    }
}
