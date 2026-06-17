package cn.zswltech.mithras.budget.application.port;

import java.time.LocalDate;
import java.util.List;

public interface BudgetPaymentFactPort {

    List<BudgetContractPaymentFactSnapshot> listContractPayInfoBetween(LocalDate startDate, LocalDate endDate);

    List<BudgetContractPaymentFactSnapshot> listContractPayInfoBeforeTargetDate(LocalDate targetDate);

    List<BudgetPaymentActualSnapshot> listPaymentActualBetween(LocalDate startDate, LocalDate endDate);
}
