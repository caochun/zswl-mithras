package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@AllArgsConstructor
@Getter
public enum AfterLeaseCheckReportTypeEnum implements PullDown {
    PUBLIC("公用事业、民生消费类"),
    NON_PUBLIC("产业类"),
    LOW_RISK("低风险业务"),
    BUS("公交类"),
    STATE_OWNED_ASSET("国有资产类");

    private final String display;

    public static AfterLeaseCheckReportTypeEnum ofName(String name) {
        for (AfterLeaseCheckReportTypeEnum value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
