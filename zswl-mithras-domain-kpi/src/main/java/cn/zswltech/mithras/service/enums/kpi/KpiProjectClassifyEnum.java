package cn.zswltech.mithras.service.enums.kpi;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum KpiProjectClassifyEnum implements PullDown {
    FACTORY("厂商类"),
    PUBLIC("公用事业类"),
    INDUSTRY("产业类");

    private final String display;


    public static KpiProjectClassifyEnum find(String name) {
        for (KpiProjectClassifyEnum item : values()) {
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
