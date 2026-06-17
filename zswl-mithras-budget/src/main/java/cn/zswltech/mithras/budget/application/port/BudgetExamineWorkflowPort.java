package cn.zswltech.mithras.budget.application.port;

public interface BudgetExamineWorkflowPort {

    void startBudgetExamineFlow(Long budgetExamineId, Long submitUserId);

    void ccHumanResourcesSupervisor(String processInstanceId);
}
