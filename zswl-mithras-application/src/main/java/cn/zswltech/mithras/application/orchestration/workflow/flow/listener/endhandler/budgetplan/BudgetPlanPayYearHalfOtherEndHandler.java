package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.budgetplan;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.application.orchestration.budget.BudgetPlanPayFlowService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.YearHalfOtherPlanEventFlow;

/**
  * @Description 流程结束操作
  * @ClassName BudgetPlanPayYearHalfOtherEndHandler.java
  * @author tangxh
  * @Date 11:28
  * @Version 1.0
  **/
@Component
public class BudgetPlanPayYearHalfOtherEndHandler extends AbstractProcessEndHandler {

    @Resource
    private BudgetPlanPayFlowService budgetPlanPayFlowService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), YearHalfOtherPlanEventFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        budgetPlanPayFlowService.processEnd(endContext,false);
    }
}
