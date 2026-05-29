package cn.zswltech.mithras.service.facade.fund;

import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.service.mapper.model.fund.*;
import cn.zswltech.mithras.service.mapper.model.fund.financing.*;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.*;
import cn.zswltech.mithras.service.service.fund.*;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FundFacadeImpl implements FundFacade {

    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private FundFinancingCreditRefService fundFinancingCreditRefService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;

    // ========== FundOrganization ==========

    @Override
    public List<FundOrganization> getOrganizationsByFinancingId(Long financingId) {
        return fundOrganizationService.getByFinancingId(financingId);
    }

    @Override
    public List<FundOrganization> getOrganizationsByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundOrganizationService.listByIds(ids);
    }

    @Override
    public Map<Long, String> getOrganizationNamesByIds(Collection<Long> ids) {
        return fundOrganizationService.getNamesByIds(ids);
    }

    @Override
    public Map<Long, List<FundOrganization>> getOrganizationsBatchByFinancingIds(Collection<Long> financingIds) {
        return fundOrganizationService.getBatchByFinancingId(financingIds);
    }

    @Override
    public List<FundOrganization> listOrganizations(LambdaQueryWrapper<FundOrganization> wrapper) {
        return fundOrganizationService.list(wrapper);
    }

    // ========== FundFinancingBaseInfo ==========

    @Override
    public FundFinancingBaseInfo getFinancingById(Long id) {
        return fundFinancingBaseInfoService.getById(id);
    }

    @Override
    public List<FundFinancingBaseInfo> listFinancingByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundFinancingBaseInfoService.listByIds(ids);
    }

    @Override
    public List<FundFinancingBaseInfo> listFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper) {
        return fundFinancingBaseInfoService.list(wrapper);
    }

    @Override
    public long countFinancing(LambdaQueryWrapper<FundFinancingBaseInfo> wrapper) {
        return fundFinancingBaseInfoService.count(wrapper);
    }

    @Override
    public void updateFinancingHasPledge(Long financingId, YesOrNoNumberEnum hasPledge) {
        fundFinancingBaseInfoService.updateHasPledge(financingId, hasPledge);
    }

    // ========== FundDirectFinancingBaseInfo ==========

    @Override
    public FundDirectFinancingBaseInfo getDirectFinancingById(Long id) {
        return fundDirectFinancingBaseInfoService.getById(id);
    }

    @Override
    public List<FundDirectFinancingBaseInfo> listDirectFinancingByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundDirectFinancingBaseInfoService.listByIds(ids);
    }

    @Override
    public List<FundDirectFinancingBaseInfo> listDirectFinancing(LambdaQueryWrapper<FundDirectFinancingBaseInfo> wrapper) {
        return fundDirectFinancingBaseInfoService.list(wrapper);
    }

    // ========== FundCredit ==========

    @Override
    public FundCredit getCreditById(Long id) {
        return fundCreditService.getById(id);
    }

    @Override
    public List<FundCredit> listCredits(LambdaQueryWrapper<FundCredit> wrapper) {
        return fundCreditService.list(wrapper);
    }

    // ========== FundFinancingCreditRef ==========

    @Override
    public List<FundFinancingCreditRef> getCreditRefsByFinancingId(Long financingId) {
        return fundFinancingCreditRefService.list(
                Wrappers.<FundFinancingCreditRef>lambdaQuery().eq(FundFinancingCreditRef::getFinancingId, financingId));
    }

    // ========== FundFinancingPledgeInfo ==========

    @Override
    public List<FundFinancingPledgeInfo> listPledgeInfo(LambdaQueryWrapper<FundFinancingPledgeInfo> wrapper) {
        return fundFinancingPledgeInfoService.list(wrapper);
    }

    @Override
    public List<FundFinancingPledgeInfo> findContractPledgeList(Long contractId) {
        return fundFinancingPledgeInfoService.findContractPledgeList(contractId);
    }

    // ========== FundDirectFinancingPledgeInfo ==========

    @Override
    public List<FundDirectFinancingPledgeInfo> listDirectPledgeInfo(LambdaQueryWrapper<FundDirectFinancingPledgeInfo> wrapper) {
        return fundDirectFinancingPledgeInfoService.list(wrapper);
    }

    // ========== FundReceiptRepayBaseInfo ==========

    @Override
    public FundReceiptRepayBaseInfo getReceiptRepayById(Long id) {
        return fundReceiptRepayBaseInfoService.getById(id);
    }

    @Override
    public List<FundReceiptRepayBaseInfo> listReceiptRepay(LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper) {
        return fundReceiptRepayBaseInfoService.list(wrapper);
    }

    @Override
    public List<FundReceiptRepayBaseInfo> listReceiptRepayByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return fundReceiptRepayBaseInfoService.listByIds(ids);
    }

    @Override
    public Map<Long, Long> queryRemainingAmount(Collection<Long> financingIds, FinancingTypeEnum type) {
        return fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIds, type);
    }

    // ========== FundReceiptRepayCashFlow ==========

    @Override
    public List<FundReceiptRepayCashFlow> listCashFlow(LambdaQueryWrapper<FundReceiptRepayCashFlow> wrapper) {
        return fundReceiptRepayCashFlowService.list(wrapper);
    }

    // ========== FundFinancingPlan ==========

    @Override
    public List<FundFinancingPlan> listFinancingPlan(LambdaQueryWrapper<FundFinancingPlan> wrapper) {
        return fundFinancingPlanService.list(wrapper);
    }

    // ========== FundDirectFinancingRepayActual ==========

    @Override
    public List<FundDirectFinancingRepayActual> listDirectRepayActual(LambdaQueryWrapper<FundDirectFinancingRepayActual> wrapper) {
        return fundDirectFinancingRepayActualService.list(wrapper);
    }
}
