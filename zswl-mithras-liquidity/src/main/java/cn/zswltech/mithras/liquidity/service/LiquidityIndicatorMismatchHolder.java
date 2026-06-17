package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.liquidity.bo.LiquidityCollectionRecordSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityContractBaseSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityContractRentSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityDirectFinancingSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFinancingOrganizationSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFinancingPledgeSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFinancingSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFundReceiptFlowPlanSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFundReceiptRepaySnapshot;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
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
public class LiquidityIndicatorMismatchHolder {

    private LiquidityIndicatorMismatchHolder() {}

    /**
     * 当前时间
     */
    public static LocalDate NOW = null;

    /**
     * 账户
     */
    public static Map<LocalDate, Map<Long, AccountBalanceBaseInfo>> ACCOUNT_BALANCE_BASE_INFO = new HashMap<>();

    /**
     * 合同基本表信息，key: 合同id (起租)
     */
    public static Map<Long, LiquidityContractBaseSnapshot> CONTRACT_BASE_INFO = new HashMap<>();

    /**
     * 合同实际租金表，key: 应付日 (起租)
     */
    public static Map<LocalDate, List<LiquidityContractRentSnapshot>> CONTRACT_RENT_ACTUAL = new HashMap<>();

    /**
     * 实际核销记录，key：收款id
     */
    public static Map<Long, List<LiquidityCollectionRecordSnapshot>> COLLECTION_RECORD_INFO = new HashMap<>();

    /**
     * 间融基本表，key: 间融id
     */
    public static Map<Long, LiquidityFinancingSnapshot> FUND_FINANCING_BASE_INFO = new HashMap<>();

    /**
     * 间融质押监管
     */
    public static Map<Long, List<LiquidityFinancingPledgeSnapshot>> FUND_FINANCING_PLEDGE_INFO = new HashMap<>();

    /**
     * 直融基本表，key: 直融id
     */
    public static Map<Long, LiquidityDirectFinancingSnapshot> FUND_DIRECT_FINANCING_BASE_INFO = new HashMap<>();

    /**
     * 直融质押监管
     */
    public static Map<Long, List<LiquidityFinancingPledgeSnapshot>> FUND_DIRECT_FINANCING_PLEDGE_INFO = new HashMap<>();

    /**
     * 还本付息基本表，key: 间融id
     */
    public static Map<Long, LiquidityFundReceiptRepaySnapshot> FUND_RECEIPT_REPAY_BASE_INFO = new HashMap<>();

    /**
     * 还本付息现金流 间融，key: 应付日
     */
    public static Map<LocalDate, Map<String, List<LiquidityFundReceiptFlowPlanSnapshot>>> FUND_RECEIPT_FLOW_PLAN = new HashMap<>();

    /**
     * 间融机构
     */
    public static Map<Long, List<LiquidityFinancingOrganizationSnapshot>> FUND_ORGANIZATION_INFO = new HashMap<>();

}
