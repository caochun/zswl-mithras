package cn.zswltech.mithras.fund.domain.enums.financing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
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
