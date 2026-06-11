package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.fund.direct.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.direct.mapper.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.fund.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.liquidity.mapper.model.AccountBalanceBaseInfo;
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
    public static Map<Long, ContractBaseInfo> CONTRACT_BASE_INFO = new HashMap<>();

    /**
     * 合同实际租金表，key: 应付日 (起租)
     */
    public static Map<LocalDate, List<ContractRentActual>> CONTRACT_RENT_ACTUAL = new HashMap<>();

    /**
     * 实际核销记录，key：收款id
     */
    public static Map<Long, List<CollectionRecordInfo>> COLLECTION_RECORD_INFO = new HashMap<>();

    /**
     * 间融基本表，key: 间融id
     */
    public static Map<Long, FundFinancingBaseInfo> FUND_FINANCING_BASE_INFO = new HashMap<>();

    /**
     * 间融质押监管
     */
    public static Map<Long, List<FundFinancingPledgeInfo>> FUND_FINANCING_PLEDGE_INFO = new HashMap<>();

    /**
     * 直融基本表，key: 直融id
     */
    public static Map<Long, FundDirectFinancingBaseInfo> FUND_DIRECT_FINANCING_BASE_INFO = new HashMap<>();

    /**
     * 直融质押监管
     */
    public static Map<Long, List<FundDirectFinancingPledgeInfo>> FUND_DIRECT_FINANCING_PLEDGE_INFO = new HashMap<>();

    /**
     * 还本付息基本表，key: 间融id
     */
    public static Map<Long, FundReceiptRepayBaseInfo> FUND_RECEIPT_REPAY_BASE_INFO = new HashMap<>();

    /**
     * 还本付息现金流 间融，key: 应付日
     */
    public static Map<LocalDate, Map<String ,List<FundReceiptFlowPlan>>> FUND_RECEIPT_FLOW_PLAN = new HashMap<>();

    /**
     * 间融机构
     */
    public static Map<Long, List<FundOrganization>> FUND_ORGANIZATION_INFO = new HashMap<>();

}
