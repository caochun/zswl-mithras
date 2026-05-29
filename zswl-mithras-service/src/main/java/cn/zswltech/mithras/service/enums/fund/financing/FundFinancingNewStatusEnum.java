package cn.zswltech.mithras.service.enums.fund.financing;

import cn.zswltech.mithras.common.enums.PullDown;
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
public enum FundFinancingNewStatusEnum implements PullDown {
    NEW("新建",10),
    EFFECT("生效",20),
    CARRY_INTEREST("起息",30);

    private final String display;
    private final Integer sort;

    @Override
    public String display() {
        return this.display;
    }

    public static FundFinancingNewStatusEnum finaByName(String name) {
        for (FundFinancingNewStatusEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
