package cn.zswltech.mithras.application.orchestration.adapter.budget;

import cn.zswltech.mithras.application.orchestration.adapter.budget.mapper.BudgetPaymentFactMapper;
import cn.zswltech.mithras.budget.application.port.BudgetContractPaymentFactSnapshot;
import cn.zswltech.mithras.budget.application.port.BudgetPaymentActualSnapshot;
import cn.zswltech.mithras.budget.application.port.BudgetPaymentFactPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Component
public class BudgetPaymentFactPortAdapter implements BudgetPaymentFactPort {

    @Resource
    private BudgetPaymentFactMapper budgetPaymentFactMapper;

    @Override
    public List<BudgetContractPaymentFactSnapshot> listContractPayInfoBetween(LocalDate startDate, LocalDate endDate) {
        return budgetPaymentFactMapper.listContractPayInfoBetween(startDate, endDate);
    }

    @Override
    public List<BudgetContractPaymentFactSnapshot> listContractPayInfoBeforeTargetDate(LocalDate targetDate) {
        return budgetPaymentFactMapper.listContractPayInfoBeforeTargetDate(targetDate);
    }

    @Override
    public List<BudgetPaymentActualSnapshot> listPaymentActualBetween(LocalDate startDate, LocalDate endDate) {
        return budgetPaymentFactMapper.listPaymentActualBetween(startDate, endDate);
    }
}
