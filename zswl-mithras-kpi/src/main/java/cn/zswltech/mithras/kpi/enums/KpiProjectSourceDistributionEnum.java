package cn.zswltech.mithras.kpi.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;

/**
 * @author dingqi
 * @date 2023/6/28
 * @description
 */
@AllArgsConstructor
public enum KpiProjectSourceDistributionEnum implements PullDown {
    NEW("新增"),
    HISTORY("翻单")
    ;
    private final String display;

    public static KpiProjectSourceDistributionEnum find(String name) {
        for (KpiProjectSourceDistributionEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
