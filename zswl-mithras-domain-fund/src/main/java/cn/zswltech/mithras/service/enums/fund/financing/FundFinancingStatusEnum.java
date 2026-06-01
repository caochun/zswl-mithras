package cn.zswltech.mithras.service.enums.fund.financing;

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
public enum FundFinancingStatusEnum implements PullDown {
    NEW("新建",10),
    CLOSE("关闭",50),
    EFFECT("生效",20),
    CARRY_INTEREST("起息",30),
    SETTLE("结清",40);

    private final String display;
    private final Integer sort;

    @Override
    public String display() {
        return this.display;
    }

    public static FundFinancingStatusEnum finaByName(String name) {
        for (FundFinancingStatusEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
