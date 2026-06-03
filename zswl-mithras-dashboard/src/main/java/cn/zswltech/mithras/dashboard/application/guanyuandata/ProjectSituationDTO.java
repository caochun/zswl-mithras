package cn.zswltech.mithras.dashboard.application.guanyuandata;

import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/9/19
 * @description 观远BI-项目情况表数据集（字段不全，只取了一些需要使用的，后续要使用再加）
 */
@Data
public class ProjectSituationDTO implements GuanYuanColumnPopulate {
    private Long clientId;
    private String clientName;
    private Integer isRelated;
    private Long belongGroupClientId;
    private Long principalBalance;
    private Long marginBalance;

    @Override
    public void populate(Map<String, String> map) {
        this.clientId = Optional.ofNullable(map.get("client_id")).map(Long::valueOf).orElse(null);
        this.clientName = map.get("客户名称");
        this.isRelated = Optional.ofNullable(map.get("是否关联方交易")).map(Integer::valueOf).orElse(0);
        this.belongGroupClientId = Optional.ofNullable(map.get("所属集团")).map(Long::valueOf).orElse(null);
        this.principalBalance = Optional.ofNullable(map.get("剩余本金")).map(Long::valueOf).orElse(0L);
        this.marginBalance = Optional.ofNullable(map.get("租赁保证金")).map(Long::valueOf).orElse(0L);
    }
}
