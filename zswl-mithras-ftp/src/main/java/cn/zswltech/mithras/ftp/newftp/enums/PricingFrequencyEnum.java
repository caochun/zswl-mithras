package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/3/22/17:45
 * @description
 */
@Getter
@AllArgsConstructor
public enum PricingFrequencyEnum implements PullDown {
    /**
     * 定价频率
     */
    MONTHLY("月度"),
    QUARTER("季度");
    private final String display;

    public static PricingFrequencyEnum ofName(String name) {
        for (PricingFrequencyEnum anEnum : PricingFrequencyEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
