package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.budgetplan;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.application.orchestration.budget.BudgetPlanPayWeeklyReportService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.BudgetPlanPayWeeklyFlow;

/**
 * @create: 2023-03-20
 **/
@Component
public class BudgetPlanPayWeeklyReportEndHandler extends AbstractProcessEndHandler {

    @Resource
    private BudgetPlanPayWeeklyReportService budgetPlanPayWeeklyReportService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), BudgetPlanPayWeeklyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        budgetPlanPayWeeklyReportService.processEnd(endContext.getBusinessKey(), endContext.getProcessInstanceId(), endContext.getEndType());
    }

}
