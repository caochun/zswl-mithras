package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailRSP;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.liquidity.mapper.model.AccountBalanceBaseInfo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流动性指标计算公用数据集（看板）
 *
 * @author chenyifei
 * @since 2024/12/12
 */
public class LiquidityIndicatorBoardHolder {

    private LiquidityIndicatorBoardHolder() {}

    /**
     * 当前时间
     */
    public static LocalDate NOW = null;

    /**
     * 账户
     */
    public static Map<LocalDate, Map<Long, AccountBalanceBaseInfo>> ACCOUNT_BALANCE_BASE_INFO = new HashMap<>();

    /**
     * 配置
     */
    public static ParameterBaseDetailRSP PARAMETER_BASE_DETAIL = new ParameterBaseDetailRSP();

    /**
     * 实际核销记录，key：收款id
     */
    public static Map<Long, List<CollectionRecordInfo>> COLLECTION_RECORD_INFO = new HashMap<>();

}
