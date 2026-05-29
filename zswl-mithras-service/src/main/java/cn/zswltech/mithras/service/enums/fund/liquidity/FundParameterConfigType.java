package cn.zswltech.mithras.service.enums.fund.liquidity;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chenyifei
 */
@AllArgsConstructor
@Getter
public enum FundParameterConfigType implements PullDown {

    /**
     * 基础参数
     */
    LIQUIDITY_BASE("liquidityBase"),

    /**
     * 流动性参数
     */
    LIQUIDITY_INDEX("liquidityIndex"),

    /**
     * 账户余额表计算月数
     */
    ACCOUNT_BALANCE_CALCULATE_TIME("accountBalanceCalculateTime"),

    /**
     * 账户余额表计算月数
     */
    ACCOUNT_BALANCE_START_TIME("accountBalanceStartTime"),

    ;

    private String display;


    @Override
    public String display() {
        return display;
    }
}
