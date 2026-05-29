package cn.zswltech.mithras.service.facade.fund;

import cn.zswltech.mithras.service.mapper.model.fund.FundCredit;
import cn.zswltech.mithras.service.mapper.model.fund.FundCreditGuaranteeDetail;
import cn.zswltech.mithras.service.mapper.model.fund.FundGuaranteeAgency;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.*;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.*;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.service.mapper.model.fund.FundGuaranteeInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fund domain facade.
 * All cross-domain access to fund data should go through this interface.
 */
public interface FundFacade {

    // ========== FundOrganization ==========

    List<FundOrganization> getOrganizationsByFinancingId(Long financingId);
    List<FundOrganization> getOrganizationsByIds(Collection<Long> ids);
    Map<Long, String> getOrganizationNamesByIds(Collection<Long> ids);
    Map<Long, List<FundOrganization>> getOrganizationsBatchByFinancingIds(Collection<Long> financingIds);
    List<FundOrganization> listOrganizations(LambdaQueryWrapper<FundOrganization> wrapper);
    FundOrganization getOneOrganization(LambdaQueryWrapper<FundOrganization> wrapper);

    // ========== FundFinancingBaseInfo ==========

    FundFinancingBaseInfo getFinancingById(Long id);
    List<FundFinancingBaseInfo> listFinancingByIds(Collection<Long> ids);
    List<FundFinancingBaseInfo> listFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper);
    long countFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper);
    void updateFinancingHasPledge(Long financingId, YesOrNoNumberEnum hasPledge);
    void tryUpdateChangeOther(Long financingId);

    // ========== FundDirectFinancingBaseInfo ==========

    FundDirectFinancingBaseInfo getDirectFinancingById(Long id);
    List<FundDirectFinancingBaseInfo> listDirectFinancingByIds(Collection<Long> ids);
    List<FundDirectFinancingBaseInfo> listDirectFinancing(LambdaQueryWrapper<FundDirectFinancingBaseInfo> wrapper);
    void updateDirectFinancingCost(Long id);
    void syncDirectFinancingToFlowPlan(Long id);

    // ========== FundCredit ==========

    FundCredit getCreditById(Long id);
    List<FundCredit> listCreditsByIds(Collection<Long> ids);
    List<FundCredit> listCredits(LambdaQueryWrapper<FundCredit> wrapper);

    // ========== FundFinancingCreditRef ==========

    List<FundFinancingCreditRef> getCreditRefsByFinancingId(Long financingId);

    // ========== FundFinancingPledgeInfo ==========

    List<FundFinancingPledgeInfo> listPledgeInfo(LambdaQueryWrapper<FundFinancingPledgeInfo> wrapper);
    List<FundFinancingPledgeInfo> findContractPledgeList(Long contractId);
    Map<Long, List<FundFinancingPledgeInfo>> getPledgeMapByFinancings(Set<Long> financingIds);

    // ========== FundDirectFinancingPledgeInfo ==========

    List<FundDirectFinancingPledgeInfo> listDirectPledgeInfo(LambdaQueryWrapper<FundDirectFinancingPledgeInfo> wrapper);

    // ========== FundDirectFinancingProductDetail ==========

    List<FundDirectFinancingProductDetail> listDirectProductDetail(LambdaQueryWrapper<FundDirectFinancingProductDetail> wrapper);

    // ========== FundGuaranteeAgency ==========

    FundGuaranteeAgency getGuaranteeAgencyById(Long id);
    FundGuaranteeAgency getOneGuaranteeAgency(LambdaQueryWrapper<FundGuaranteeAgency> wrapper);
    List<FundGuaranteeAgency> getGuaranteeAgenciesByIds(Collection<Long> ids);
    Map<Long, Long> getUsedGuaranteeLimit(List<Long> agencyIds);

    // ========== FundGuaranteeInfo ==========

    FundGuaranteeInfo getOneGuaranteeInfo(LambdaQueryWrapper<FundGuaranteeInfo> wrapper);
    Map<Long, Long> remainingGuaranteeLimit(Set<Long> agencyIds);

    // ========== FundReceiptRepayBaseInfo ==========

    FundReceiptRepayBaseInfo getReceiptRepayById(Long id);
    FundReceiptRepayBaseInfo getOneReceiptRepay(LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper);
    List<FundReceiptRepayBaseInfo> listReceiptRepay(LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper);
    List<FundReceiptRepayBaseInfo> listReceiptRepayByIds(Collection<Long> ids);
    Map<Long, Long> queryRemainingAmount(Collection<Long> financingIds, FinancingTypeEnum type);
    void addDirectFinancingReceipt(Long directFinancingId);
    void upgradeDirectFinancingReceipt(Long receiptRepayId);
    void syncReceiptRepayToFlowPlan(Long receiptRepayId);

    // ========== FundReceiptRepayCashFlow ==========

    List<FundReceiptRepayCashFlow> listCashFlow(LambdaQueryWrapper<FundReceiptRepayCashFlow> wrapper);

    // ========== FundReceiptFlowDetail ==========

    List<FundReceiptFlowDetail> listReceiptFlowDetail(LambdaQueryWrapper<FundReceiptFlowDetail> wrapper);
    List<FundReceiptFlowDetail> listReceiptFlowDetailByCashFlowCodes(Collection<String> codes);

    // ========== FundFinancingPlan ==========

    List<FundFinancingPlan> listFinancingPlan(LambdaQueryWrapper<FundFinancingPlan> wrapper);

    // ========== FundDirectFinancingRepayActual ==========

    List<FundDirectFinancingRepayActual> listDirectRepayActual(LambdaQueryWrapper<FundDirectFinancingRepayActual> wrapper);

    // ========== FundFinancingRepayActual ==========

    List<FundFinancingRepayActual> listFinancingRepayActual(LambdaQueryWrapper<FundFinancingRepayActual> wrapper);
}
