package cn.zswltech.mithras.liquidity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Liquidity-side account type codes used by liquidity calculators.
 */
@AllArgsConstructor
@Getter
public enum LiquidityBankAccountType {
    BASE("基本户"),
    NORMAL("一般户"),
    SUPERVISION("监管户"),
    OTHER("其他");

    private final String display;
}
