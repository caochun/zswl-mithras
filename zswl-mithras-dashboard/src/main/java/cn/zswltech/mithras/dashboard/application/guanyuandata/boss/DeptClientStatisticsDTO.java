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
public class DeptClientStatisticsDTO implements GuanYuanColumnPopulate {
    private String deptName;
    private Long deptId;
    private Integer quantity;

    @Override
    public void populate(Map<String, String> map) {
        this.setDeptId(Optional.ofNullable(map.get("dept_id")).map(Long::valueOf).orElse(null));
        this.setDeptName(map.get("dept_name"));
        this.setQuantity(Optional.ofNullable(map.get("quantity")).map(Integer::valueOf).orElse(0));
    }
}
