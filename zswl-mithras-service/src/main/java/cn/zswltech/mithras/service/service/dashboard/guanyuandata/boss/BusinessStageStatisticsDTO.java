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
public class BusinessStageStatisticsDTO implements GuanYuanColumnPopulate {
    private String stage;
    private Integer quantity;

    @Override
    public void populate(Map<String, String> map) {
        this.setStage(map.get("stage"));
        this.setQuantity(Optional.ofNullable(map.get("quantity")).map(Integer::valueOf).orElse(null));
    }
}
