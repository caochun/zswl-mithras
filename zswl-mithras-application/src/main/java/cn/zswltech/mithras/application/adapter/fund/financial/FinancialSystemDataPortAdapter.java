package cn.zswltech.mithras.application.adapter.fund.financial;

import cn.zswltech.mithras.fund.application.financial.port.FinancialSystemDataPort;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.fund.application.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class FinancialSystemDataPortAdapter implements FinancialSystemDataPort {

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private FundFinancingRepayActualService financingRepayActualService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;

    @Override
    public List<FundFinancingBaseInfo> listFinancialSystemTodoList(LocalDate targetDate) {
        return financingBaseInfoService.listFinancialSystemTodoList(targetDate);
    }

    @Override
    public Map<Long, List<FundOrganization>> getOrganizationsByFinancingId(Collection<Long> financingIdList) {
        return organizationService.getBatchByFinancingId(financingIdList);
    }

    @Override
    public Map<Long, FundFinancingPlan> getPlanByFinancingIds(Collection<Long> financingIds) {
        return financingPlanService.getMapByFinancingIds(financingIds);
    }

    @Override
    public Map<Long, List<FundFinancingRepayActual>> getRepayActualByFinancingId(Collection<Long> financingIdList) {
        return financingRepayActualService.getMapByFinancingId(financingIdList);
    }

    @Override
    public Map<Long, Long> queryRemainingAmount(Collection<Long> financingIdList, FinancingTypeEnum financingTypeEnum) {
        return fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIdList, financingTypeEnum);
    }

    @Override
    public Map<Long, List<FundReceiptRepayCashFlow>> queryCashFlowByFinancingIds(Collection<Long> financingIdList,
            FinancingTypeEnum financingType) {
        return fundReceiptRepayCashFlowService.queryByFinancingIds(financingIdList, financingType);
    }

    @Override
    public Map<String, List<FundReceiptFlowDetail>> listRepayFlowDetailByReceiptRepayIds(List<Long> receiptRepayIdList) {
        return fundReceiptFlowDetailService.listByReceiptRepayIds(receiptRepayIdList);
    }
}
