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
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
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
