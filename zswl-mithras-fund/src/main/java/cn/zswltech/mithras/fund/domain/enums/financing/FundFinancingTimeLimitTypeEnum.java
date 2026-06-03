package cn.zswltech.mithras.fund.domain.enums.financing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum FundFinancingTimeLimitTypeEnum implements PullDown {
    SHORT_TERM_LOAN("短期贷款"),
    LONG_TERM_LOAN("长期贷款"),
    BOND_PAYABLE("应付债券");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static FundFinancingTimeLimitTypeEnum find(String name) {
        for (FundFinancingTimeLimitTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
