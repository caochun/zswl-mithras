package cn.zswltech.mithras.liquidity.enums;

import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityMismatchDetailRSP;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.liquidity.persistence.model.AccountBalanceBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chenyifei
 */
@AllArgsConstructor
@Getter
public enum LiquidityIndexType implements PullDown {
    /**
     * 流动性指标
     */
    LIQUIDITY_INDEX("LiquidityIndex", LiquidityIndexDetailRSP.class),

    /**
     * 流动性看板
     */
    LIQUIDITY_BOARD("LiquidityBoard", LiquidityBoardDetailRSP.class),

    /**
     * 账户余额表
     */
    ACCOUNT_BALANCE("account_balance", AccountBalanceBaseInfo.class),

    /**
     * 资金错配
     */
    LIQUIDITY_MISMATCH("liquidity_mismatch", LiquidityMismatchDetailRSP.class),

    /**
     * 手动触发指标
     */
    MANUAL_TRIGGER("manualTrigger", AccountBalanceBaseInfo.class),

    ;

    private String display;

    private Class clazz;


    @Override
    public String display() {
        return display;
    }
}
