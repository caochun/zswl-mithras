package cn.zswltech.mithras.application.orchestration.adapter.fund.financial;

import cn.zswltech.mithras.fund.application.financial.port.FinancialSystemDataPort;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
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
