package cn.zswltech.mithras.service.enums.fund.financing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@AllArgsConstructor
@Getter
public enum FundFinancingRepayWayEnum implements PullDown {
    DEBJ("等额本金"),
    DEBX("等额本息"),
    LSBQ("利随本清"),
    XXHB("先息后本"),
    BGZHK("不规则还款");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
