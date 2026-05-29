package cn.zswltech.mithras.service.facade.fund;

import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingProductDetailService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.service.mapper.model.fund.*;
import cn.zswltech.mithras.service.mapper.model.fund.financing.*;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.*;
import cn.zswltech.mithras.service.service.fund.*;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FundFacadeImpl implements FundFacade {

    @Resource private FundOrganizationService fundOrganizationService;
    @Resource private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource private FundCreditService fundCreditService;
    @Resource private FundFinancingCreditRefService fundFinancingCreditRefService;
    @Resource private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource private FundDirectFinancingProductDetailService fundDirectFinancingProductDetailService;
    @Resource private FundGuaranteeAgencyService fundGuaranteeAgencyService;
    @Resource private FundGuaranteeInfoService fundGuaranteeInfoService;
    @Resource private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource private FundReceiptFlowDetailService fundReceiptFlowDetailService;
    @Resource private FundFinancingPlanService fundFinancingPlanService;
    @Resource private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;
    @Resource private FundFinancingRepayActualService fundFinancingRepayActualService;

    // ========== FundOrganization ==========

    @Override public List<FundOrganization> getOrganizationsByFinancingId(Long financingId) {
        return fundOrganizationService.getByFinancingId(financingId);
    }
    @Override public List<FundOrganization> getOrganizationsByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundOrganizationService.listByIds(ids);
    }
    @Override public Map<Long, String> getOrganizationNamesByIds(Collection<Long> ids) {
        return fundOrganizationService.getNamesByIds(ids);
    }
    @Override public Map<Long, List<FundOrganization>> getOrganizationsBatchByFinancingIds(Collection<Long> financingIds) {
        return fundOrganizationService.getBatchByFinancingId(financingIds);
    }
    @Override public List<FundOrganization> listOrganizations(LambdaQueryWrapper<FundOrganization> wrapper) {
        return fundOrganizationService.list(wrapper);
    }
    @Override public FundOrganization getOneOrganization(LambdaQueryWrapper<FundOrganization> wrapper) {
        return fundOrganizationService.getOne(wrapper);
    }

    // ========== FundFinancingBaseInfo ==========

    @Override public FundFinancingBaseInfo getFinancingById(Long id) {
        return fundFinancingBaseInfoService.getById(id);
    }
    @Override public List<FundFinancingBaseInfo> listFinancingByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundFinancingBaseInfoService.listByIds(ids);
    }
    @Override public List<FundFinancingBaseInfo> listFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper) {
        return fundFinancingBaseInfoService.list(wrapper);
    }
    @Override public long countFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper) {
        return fundFinancingBaseInfoService.count(wrapper);
    }
    @Override public void updateFinancingHasPledge(Long financingId, YesOrNoNumberEnum hasPledge) {
        fundFinancingBaseInfoService.updateHasPledge(financingId, hasPledge);
    }
    @Override public void tryUpdateChangeOther(Long financingId) {
        fundFinancingBaseInfoService.tryUpdateChangeOther(financingId);
    }

    // ========== FundDirectFinancingBaseInfo ==========

    @Override public FundDirectFinancingBaseInfo getDirectFinancingById(Long id) {
        return fundDirectFinancingBaseInfoService.getById(id);
    }
    @Override public List<FundDirectFinancingBaseInfo> listDirectFinancingByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundDirectFinancingBaseInfoService.listByIds(ids);
    }
    @Override public List<FundDirectFinancingBaseInfo> listDirectFinancing(LambdaQueryWrapper<FundDirectFinancingBaseInfo> wrapper) {
        return fundDirectFinancingBaseInfoService.list(wrapper);
    }
    @Override public void updateDirectFinancingCost(Long id) {
        fundDirectFinancingBaseInfoService.updateFinancingCost(id);
    }
    @Override public void syncDirectFinancingToFlowPlan(Long id) {
        fundDirectFinancingBaseInfoService.syncToFlowPlan(id);
    }

    // ========== FundCredit ==========

    @Override public FundCredit getCreditById(Long id) {
        return fundCreditService.getById(id);
    }
    @Override public List<FundCredit> listCreditsByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundCreditService.listByIds(ids);
    }
    @Override public List<FundCredit> listCredits(LambdaQueryWrapper<FundCredit> wrapper) {
        return fundCreditService.list(wrapper);
    }

    // ========== FundFinancingCreditRef ==========

    @Override public List<FundFinancingCreditRef> getCreditRefsByFinancingId(Long financingId) {
        return fundFinancingCreditRefService.list(
                Wrappers.<FundFinancingCreditRef>lambdaQuery().eq(FundFinancingCreditRef::getFinancingId, financingId));
    }

    // ========== FundFinancingPledgeInfo ==========

    @Override public List<FundFinancingPledgeInfo> listPledgeInfo(LambdaQueryWrapper<FundFinancingPledgeInfo> wrapper) {
        return fundFinancingPledgeInfoService.list(wrapper);
    }
    @Override public List<FundFinancingPledgeInfo> findContractPledgeList(Long contractId) {
        return fundFinancingPledgeInfoService.findContractPledgeList(contractId);
    }
    @Override public Map<Long, List<FundFinancingPledgeInfo>> getPledgeMapByFinancings(Set<Long> financingIds) {
        return fundFinancingPledgeInfoService.getMapByFinancings(financingIds);
    }

    // ========== FundDirectFinancingPledgeInfo ==========

    @Override public List<FundDirectFinancingPledgeInfo> listDirectPledgeInfo(LambdaQueryWrapper<FundDirectFinancingPledgeInfo> wrapper) {
        return fundDirectFinancingPledgeInfoService.list(wrapper);
    }

    // ========== FundDirectFinancingProductDetail ==========

    @Override public List<FundDirectFinancingProductDetail> listDirectProductDetail(LambdaQueryWrapper<FundDirectFinancingProductDetail> wrapper) {
        return fundDirectFinancingProductDetailService.list(wrapper);
    }

    // ========== FundGuaranteeAgency ==========

    @Override public FundGuaranteeAgency getGuaranteeAgencyById(Long id) {
        return fundGuaranteeAgencyService.getById(id);
    }
    @Override public FundGuaranteeAgency getOneGuaranteeAgency(LambdaQueryWrapper<FundGuaranteeAgency> wrapper) {
        return fundGuaranteeAgencyService.getOne(wrapper);
    }
    @Override public List<FundGuaranteeAgency> getGuaranteeAgenciesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundGuaranteeAgencyService.listByIds(ids);
    }
    @Override public Map<Long, Long> getUsedGuaranteeLimit(List<Long> agencyIds) {
        return fundGuaranteeAgencyService.getUsedGuaranteeLimit(agencyIds);
    }

    // ========== FundGuaranteeInfo ==========

    @Override public FundGuaranteeInfo getOneGuaranteeInfo(LambdaQueryWrapper<FundGuaranteeInfo> wrapper) {
        return fundGuaranteeInfoService.getOne(wrapper);
    }
    @Override public Map<Long, Long> remainingGuaranteeLimit(Set<Long> agencyIds) {
        return fundGuaranteeInfoService.remainingGuaranteeLimit(agencyIds);
    }

    // ========== FundReceiptRepayBaseInfo ==========

    @Override public FundReceiptRepayBaseInfo getReceiptRepayById(Long id) {
        return fundReceiptRepayBaseInfoService.getById(id);
    }
    @Override public FundReceiptRepayBaseInfo getOneReceiptRepay(LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper) {
        return fundReceiptRepayBaseInfoService.getOne(wrapper);
    }
    @Override public List<FundReceiptRepayBaseInfo> listReceiptRepay(LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper) {
        return fundReceiptRepayBaseInfoService.list(wrapper);
    }
    @Override public List<FundReceiptRepayBaseInfo> listReceiptRepayByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundReceiptRepayBaseInfoService.listByIds(ids);
    }
    @Override public Map<Long, Long> queryRemainingAmount(Collection<Long> financingIds, FinancingTypeEnum type) {
        return fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIds, type);
    }
    @Override public void addDirectFinancingReceipt(Long directFinancingId) {
        fundReceiptRepayBaseInfoService.addDirectFinancingReceipt(directFinancingId);
    }
    @Override public void upgradeDirectFinancingReceipt(Long receiptRepayId) {
        fundReceiptRepayBaseInfoService.upgradeDirectFinancingReceipt(receiptRepayId);
    }
    @Override public void syncReceiptRepayToFlowPlan(Long receiptRepayId) {
        fundReceiptRepayBaseInfoService.syncToFlowPlan(receiptRepayId);
    }

    // ========== FundReceiptRepayCashFlow ==========

    @Override public List<FundReceiptRepayCashFlow> listCashFlow(LambdaQueryWrapper<FundReceiptRepayCashFlow> wrapper) {
        return fundReceiptRepayCashFlowService.list(wrapper);
    }

    // ========== FundReceiptFlowDetail ==========

    @Override public List<FundReceiptFlowDetail> listReceiptFlowDetail(LambdaQueryWrapper<FundReceiptFlowDetail> wrapper) {
        return fundReceiptFlowDetailService.list(wrapper);
    }
    @Override public List<FundReceiptFlowDetail> listReceiptFlowDetailByCashFlowCodes(Collection<String> codes) {
        return fundReceiptFlowDetailService.listByCashFlowCodes(codes);
    }

    // ========== FundFinancingPlan ==========

    @Override public List<FundFinancingPlan> listFinancingPlan(LambdaQueryWrapper<FundFinancingPlan> wrapper) {
        return fundFinancingPlanService.list(wrapper);
    }

    // ========== FundDirectFinancingRepayActual ==========

    @Override public List<FundDirectFinancingRepayActual> listDirectRepayActual(LambdaQueryWrapper<FundDirectFinancingRepayActual> wrapper) {
        return fundDirectFinancingRepayActualService.list(wrapper);
    }

    // ========== FundFinancingRepayActual ==========

    @Override public List<FundFinancingRepayActual> listFinancingRepayActual(LambdaQueryWrapper<FundFinancingRepayActual> wrapper) {
        return fundFinancingRepayActualService.list(wrapper);
    }
}
