package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.liquidity.bo.LiquidityCollectionPlanSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityCollectionRecordSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityCreditLimitSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFinancingPledgeSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFundReceiptFlowDetailSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFundReceiptFlowPlanSnapshot;
import cn.zswltech.mithras.liquidity.persistence.model.AccountBalanceBaseInfo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流动性指标计算公用数据集（指标）
 *
 * @author chenyifei
 * @since 2024/12/12
 */
public class LiquidityIndicatorIndexHolder {

    private LiquidityIndicatorIndexHolder() {}

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
    public static Map<String, ParameterIndexDetailRSP> PARAMETER_INDEX_DETAIL = new HashMap<>();


    /**
     * 收款表基本信息，key: 合同id (起租，过滤第零期)
     */
    public static Map<LocalDate, List<LiquidityCollectionPlanSnapshot>> COLLECTION_BASE_INFO = new HashMap<>();

    /**
     * 实际核销记录，key：收款id
     */
    public static Map<Long, List<LiquidityCollectionRecordSnapshot>> COLLECTION_RECORD_INFO = new HashMap<>();


    /**
     * 间融质押监管
     */
    public static Map<Long, List<LiquidityFinancingPledgeSnapshot>> FUND_FINANCING_PLEDGE_INFO = new HashMap<>();


    /**
     * 直融质押监管
     */
    public static Map<Long, List<LiquidityFinancingPledgeSnapshot>> FUND_DIRECT_FINANCING_PLEDGE_INFO = new HashMap<>();


    /**
     * 还本付息现金流 间融，key: 应付日
     */
    public static Map<LocalDate, Map<String, List<LiquidityFundReceiptFlowPlanSnapshot>>> FUND_RECEIPT_FLOW_PLAN = new HashMap<>();


    /**
     * 实际核销记录 key: 还本付息id
     */
    public static Map<Long, Map<String, List<LiquidityFundReceiptFlowDetailSnapshot>>> FUND_RECEIPT_FLOW_DETAIL = new HashMap<>();

    /**
     * 授信额度
     */
    public static Map<Long, LiquidityCreditLimitSnapshot> CREDIT_LIMIT_DETAIL = new HashMap<>();


}
