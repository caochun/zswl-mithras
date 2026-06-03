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
public class StageClientStatisticsDTO implements GuanYuanColumnPopulate {
    private String title;
    private Integer quantity;

    @Override
    public void populate(Map<String, String> map) {
        this.setTitle(map.get("title"));
        this.setQuantity(Optional.ofNullable(map.get("quantity")).map(Integer::valueOf).orElse(0));
    }
}
