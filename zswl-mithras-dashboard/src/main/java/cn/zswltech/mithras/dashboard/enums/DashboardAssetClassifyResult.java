package cn.zswltech.mithras.dashboard.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum DashboardAssetClassifyResult {
    NORMAL("正常"),
    ATTENTION("关注"),
    SECONDARY("次级"),
    SUSPICIOUS("可疑"),
    LOSS("损失");

    private final String display;

    private static final Map<String, DashboardAssetClassifyResult> MAP = new HashMap<>();

    static {
        for (DashboardAssetClassifyResult item : values()) {
            MAP.put(item.name(), item);
        }
    }

    public static DashboardAssetClassifyResult of(String name) {
        return MAP.get(name);
    }
}
