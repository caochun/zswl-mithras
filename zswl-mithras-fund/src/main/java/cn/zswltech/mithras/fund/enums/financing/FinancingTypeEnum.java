package cn.zswltech.mithras.fund.enums.financing;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/6/26/16:03
 * @description
 */
@Getter
@AllArgsConstructor
public enum FinancingTypeEnum implements PullDown {
    DIRECT("直融"),
    INDIRECT("间融"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
