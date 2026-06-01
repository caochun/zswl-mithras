package cn.zswltech.mithras.service.enums.fund.liquidity;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chenyifei
 */
@AllArgsConstructor
@Getter
public enum FundParameterLevelType implements PullDown {

    /**
     * 一级预警
     */
    RED("red"),

    /**
     * 二级预警
     */
    YELLOW("yellow"),
    ;

    private String display;


    @Override
    public String display() {
        return display;
    }
}
