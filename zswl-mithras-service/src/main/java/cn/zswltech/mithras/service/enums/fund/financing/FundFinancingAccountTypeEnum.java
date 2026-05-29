package cn.zswltech.mithras.service.enums.fund.financing;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * @author zswl
 */

@AllArgsConstructor
@Getter
public enum FundFinancingAccountTypeEnum implements PullDown {
    REPAY_PRINCIPAL("仅还本",1),
    REPAY_INTEREST("仅付息",3),
    REPAY_PRINCIPAL_AND_INTEREST("还本付息",2),
    ;

    private final String display;
    private final Integer sort;

    @Override
    public String display() {
        return this.display;
    }

    public static FundFinancingAccountTypeEnum finaByName(String name) {
        for (FundFinancingAccountTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
