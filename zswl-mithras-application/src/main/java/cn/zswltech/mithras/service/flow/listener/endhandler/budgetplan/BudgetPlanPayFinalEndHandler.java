package cn.zswltech.mithras.service.flow.listener.endhandler.budgetplan;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayFlowService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.FinalPlanEventFlow;

/**
  * @Description 流程结束操作
  * @ClassName BudgetPlanPayYearHalfOtherEndHandler.java
  * @author tangxh
  * @Date 11:28
  * @Version 1.0
  **/
@Component
public class BudgetPlanPayFinalEndHandler extends AbstractProcessEndHandler {

    @Resource
    private BudgetPlanPayFlowService budgetPlanPayFlowService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), FinalPlanEventFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        budgetPlanPayFlowService.processFinalEnd(endContext);
    }
}
