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

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    // ========== FundFinancingBaseInfo ==========

    FundFinancingBaseInfo getFinancingById(Long id);
    List<FundFinancingBaseInfo> listFinancingByIds(Collection<Long> ids);
    List<FundFinancingBaseInfo> listFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper);
    long countFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper);
    void updateFinancingHasPledge(Long financingId, YesOrNoNumberEnum hasPledge);

    // ========== FundDirectFinancingBaseInfo ==========

    FundDirectFinancingBaseInfo getDirectFinancingById(Long id);
    List<FundDirectFinancingBaseInfo> listDirectFinancingByIds(Collection<Long> ids);
    List<FundDirectFinancingBaseInfo> listDirectFinancing(LambdaQueryWrapper<FundDirectFinancingBaseInfo> wrapper);

    // ========== FundCredit ==========

    FundCredit getCreditById(Long id);
    List<FundCredit> listCredits(LambdaQueryWrapper<FundCredit> wrapper);

    // ========== FundFinancingCreditRef ==========

    List<FundFinancingCreditRef> getCreditRefsByFinancingId(Long financingId);

    // ========== FundFinancingPledgeInfo ==========

    List<FundFinancingPledgeInfo> listPledgeInfo(LambdaQueryWrapper<FundFinancingPledgeInfo> wrapper);
    List<FundFinancingPledgeInfo> findContractPledgeList(Long contractId);

    // ========== FundDirectFinancingPledgeInfo ==========

    List<FundDirectFinancingPledgeInfo> listDirectPledgeInfo(LambdaQueryWrapper<FundDirectFinancingPledgeInfo> wrapper);

    // ========== FundReceiptRepayBaseInfo ==========

    FundReceiptRepayBaseInfo getReceiptRepayById(Long id);
    List<FundReceiptRepayBaseInfo> listReceiptRepay(LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper);
    List<FundReceiptRepayBaseInfo> listReceiptRepayByIds(Collection<Long> ids);
    Map<Long, Long> queryRemainingAmount(Collection<Long> financingIds, FinancingTypeEnum type);

    // ========== FundReceiptRepayCashFlow ==========

    List<FundReceiptRepayCashFlow> listCashFlow(LambdaQueryWrapper<FundReceiptRepayCashFlow> wrapper);

    // ========== FundFinancingPlan ==========

    List<FundFinancingPlan> listFinancingPlan(LambdaQueryWrapper<FundFinancingPlan> wrapper);

    // ========== FundDirectFinancingRepayActual ==========

    List<FundDirectFinancingRepayActual> listDirectRepayActual(LambdaQueryWrapper<FundDirectFinancingRepayActual> wrapper);
}
