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
public class AssetsOverviewItemDTO implements GuanYuanColumnPopulate {
    private String itemName;
    private Long itemAmount;

    @Override
    public void populate(Map<String, String> map) {
        this.setItemName(map.get("item_name"));
        this.setItemAmount(Optional.ofNullable(map.get("item_amount")).map(Long::valueOf).orElse(0L));
    }
}
