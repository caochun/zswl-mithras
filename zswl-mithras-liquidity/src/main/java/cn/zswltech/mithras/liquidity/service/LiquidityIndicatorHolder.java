package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataSpecialDate;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.liquidity.model.FundParameterConfig;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 流动性指标计算公用数据集
 *
 * @author chenyifei
 * @since 2024/12/12
 */
public class LiquidityIndicatorHolder {

    private LiquidityIndicatorHolder() {}

    /**
     * 当前时间
     */
    public static LocalDate NOW = null;

    /**
     * 我方账户基本信息 key: 账户id
     */
    public static Map<Long, BaseDataBankAccount> BASE_DATA_BANK_ACCOUNT = new HashMap<>();

    /**
     * 1202021219900394595 默认账户
     */
    public static BaseDataBankAccount DEFAULT_ACCOUNT = new BaseDataBankAccount();

    /**
     * 账户
     */
    public static Map<LocalDate, Map<Long, AccountBalanceBaseInfo>> ACCOUNT_BALANCE_BASE_INFO = new HashMap<>();

    /**
     * 节假日调休表
     */
    public static Map<LocalDate ,BaseDataSpecialDate> BASE_DATA_SPECIAL_DATE = new HashMap<>();

    /**
     * 配置
     */
    public static ParameterBaseDetailRSP PARAMETER_BASE_DETAIL = new ParameterBaseDetailRSP();
    public static FundParameterConfig ACCOUNT_PARAMETER_CONFIG_START_TIME = new FundParameterConfig();
    public static FundParameterConfig ACCOUNT_PARAMETER_CONFIG_CALCULATE_MONTH = new FundParameterConfig();
    public static Map<String, ParameterIndexDetailRSP> PARAMETER_INDEX_DETAIL = new HashMap<>();


    /**
     * 回款账户参数配置表 key: 账户id
     */
    public static Map<Long, List<FundFinancingAccountSetting>> FUND_FINANCING_ACCOUNT_SETTING = new HashMap<>();

    /**
     * 合同基本表信息，key: 合同id (起租)
     */
    public static Map<Long, ContractBaseInfo> CONTRACT_BASE_INFO = new HashMap<>();

    /**
     * 合同实际租金表，key: 应付日 (起租)
     */
    public static Map<LocalDate, List<ContractRentActual>> CONTRACT_RENT_ACTUAL = new HashMap<>();

    /**
     * 合同的最大期项，key: 合同id (起租) value：最大期项
     */
    public static Map<Long, Integer> COLLECTION_BASE_INFO_MAX_PHASE= new HashMap<>();

    /**
     * 合同报价方案, key: 合同id（起租）
     */
    public static Map<Long, ContractPriceDetailRSP> CONTRACT_PRICE_DETAIL = new HashMap<>();

    /**
     * 收款表基本信息，key: 合同id (起租，过滤第零期)
     */
    public static Map<LocalDate, List<CollectionBaseInfo>> COLLECTION_BASE_INFO = new HashMap<>();

    /**
     * 实际核销记录，key：收款id
     */
    public static Map<Long, List<CollectionRecordInfo>> COLLECTION_RECORD_INFO = new HashMap<>();

    /**
     * 项目合同账户 ，key: 合同id
     */
    public static Map<Long, Long> CONTRACT_ACCOUNT = new HashMap<>();

    /**
     * 间融基本表，key: 间融id
     */
    public static Map<Long, FundFinancingBaseInfo> FUND_FINANCING_BASE_INFO = new HashMap<>();

    /**
     * 间融我司还款账户，key: 银行账号id
     */
    public static Map<Long, List<FundFinancingPayAccount>> FUND_FINANCING_PAY_ACCOUNT = new HashMap<>();

    /**
     * 间融实际还款计划，key: 支付日(起息)
     */
    public static Map<LocalDate, List<FundFinancingRepayActual>> FUND_FINANCING_REPAY_ACTUAL = new HashMap<>();

    /**
     * 直融基本表，key: 直融id
     */
    public static Map<Long, FundDirectFinancingBaseInfo> FUND_DIRECT_FINANCING_BASE_INFO = new HashMap<>();

    /**
     * 直融实际还款计划，key: 支付日
     */
    public static Map<LocalDate, List<FundDirectFinancingRepayActual>> FUND_DIRECT_FINANCING_REPAY_ACTUAL = new HashMap<>();

    /**
     * 逾期合同
     */
    public static List<Long> CONTRACT_IS_OVERDUE = new ArrayList<>();


    /**
     * 还本付息现金流 间融，key: 应付日
     */
    public static Map<Long ,FundReceiptRepayBaseInfo> FUND_RECEIPT_REPAY_BASE_INFO = new HashMap<>();

    /**
     * 还本付息现金流 间融，key: 应付日
     */
    public static Map<LocalDate, List<FundReceiptFlowPlan>> FUND_RECEIPT_FLOW_PLAN = new HashMap<>();

}
