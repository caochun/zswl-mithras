package cn.zswltech.mithras.others.service.budget;

import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitAddREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.budget.BudgetExamineBenefitService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @ClassName FinanceFlowAutoWriteOffTest
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/26 18:04
 * @Version 1.0
 **/
public class BudgetPlanCostTest extends ApplicationTest {
    @Resource
    private BudgetExamineBenefitService budgetExamineBenefitService;

    @Test
    public void budgetExamineBenefitServiceAdd () {
        BudgetExamineBenefitAddREQ benefitAddREQ = new BudgetExamineBenefitAddREQ();
        benefitAddREQ.setBudgetExamineId(6L);
        benefitAddREQ.setBudgetExamineYear(2025);
        benefitAddREQ.setBudgetExamineMonth(8);
        budgetExamineBenefitService.add(benefitAddREQ);
    }
}
