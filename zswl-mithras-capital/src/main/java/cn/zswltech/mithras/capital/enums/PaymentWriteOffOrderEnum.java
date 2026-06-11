package cn.zswltech.mithras.capital.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/27/15:15
 * @description 现金流核销顺序
 */
@Getter
@AllArgsConstructor
public enum PaymentWriteOffOrderEnum implements PullDown {
    EARNEST_MONEY("保证金", 1),
    INVESTMENTS_FUNDS("投放款", 2),
    RETENTION_MONEY("质保金", 3),
    ;

    private final String display;
    private final Integer sort;

    @Override
    public String display() {
        return display;
    }
}
