package cn.zswltech.mithras.service.service.liquiditymanage;

import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataSpecialDate;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.FundFinancingAccountSetting;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.FundParameterConfig;
import cn.zswltech.mithras.service.service.bo.CreditLimitDetailBO;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public static Map<LocalDate, List<CollectionBaseInfo>> COLLECTION_BASE_INFO = new HashMap<>();

    /**
     * 实际核销记录，key：收款id
     */
    public static Map<Long, List<CollectionRecordInfo>> COLLECTION_RECORD_INFO = new HashMap<>();


    /**
     * 间融质押监管
     */
    public static Map<Long, List<FundFinancingPledgeInfo>> FUND_FINANCING_PLEDGE_INFO = new HashMap<>();


    /**
     * 直融质押监管
     */
    public static Map<Long, List<FundDirectFinancingPledgeInfo>> FUND_DIRECT_FINANCING_PLEDGE_INFO = new HashMap<>();


    /**
     * 还本付息现金流 间融，key: 应付日
     */
    public static Map<LocalDate, Map<String ,List<FundReceiptFlowPlan>>> FUND_RECEIPT_FLOW_PLAN = new HashMap<>();


    /**
     * 实际核销记录 key: 还本付息id
     */
    public static Map<Long, Map<String ,List<FundReceiptFlowDetail>>> FUND_RECEIPT_FLOW_DETAIL = new HashMap<>();

    /**
     * 授信额度
     */
    public static Map<Long, CreditLimitDetailBO> CREDIT_LIMIT_DETAIL = new HashMap<>();


}
