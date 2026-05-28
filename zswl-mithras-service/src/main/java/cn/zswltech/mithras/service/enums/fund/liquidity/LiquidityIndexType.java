package cn.zswltech.mithras.service.enums.fund.liquidity;

import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityMismatchDetailRSP;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
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
