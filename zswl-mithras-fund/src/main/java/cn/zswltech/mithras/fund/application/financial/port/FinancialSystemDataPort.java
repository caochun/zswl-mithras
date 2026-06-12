package cn.zswltech.mithras.fund.application.financial.port;

import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.persistence.model.FundOrganization;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashFlow;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FinancialSystemDataPort {

    List<FundFinancingBaseInfo> listFinancialSystemTodoList(LocalDate targetDate);

    Map<Long, List<FundOrganization>> getOrganizationsByFinancingId(Collection<Long> financingIdList);

    Map<Long, FundFinancingPlan> getPlanByFinancingIds(Collection<Long> financingIds);

    Map<Long, List<FundFinancingRepayActual>> getRepayActualByFinancingId(Collection<Long> financingIdList);

    Map<Long, Long> queryRemainingAmount(Collection<Long> financingIdList, FinancingTypeEnum financingTypeEnum);

    Map<Long, List<FundReceiptRepayCashFlow>> queryCashFlowByFinancingIds(Collection<Long> financingIdList,
            FinancingTypeEnum financingType);

    Map<String, List<FundReceiptFlowDetail>> listRepayFlowDetailByReceiptRepayIds(List<Long> receiptRepayIdList);
}
