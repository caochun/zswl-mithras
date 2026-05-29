package cn.zswltech.mithras.service.enums.fund.financing;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@AllArgsConstructor
@Getter
public enum FundFinancingSceneEnum implements PullDown {
    NEW("新建"),
    CARRY_INTEREST("起息"),
    CHANGE_LPR("贷后变更-LPR"),
    CHANGE_SETTLE_EARLY("贷后变更-提前结清"),
    CHANGE_OTHER("贷后变更-其他");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static FundFinancingSceneEnum find(String name) {
        for (FundFinancingSceneEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
