package cn.zswltech.mithras.service.flow.listener.endhandler.budgetplan;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.budget.application.BudgetExamineFlowService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.BudgetExamineFlow;

/**
 * @author tangxh
 * @Description 流程结束操作
 * @ClassName BudgetPlanPayYearHalfOtherEndHandler.java
 * @Date 11:28
 * @Version 1.0
 **/
@Component
public class BudgetExamineEndHandler extends AbstractProcessEndHandler {

    @Resource
    BudgetExamineFlowService budgetExamineFlowService;


    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), BudgetExamineFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        budgetExamineFlowService.copyFlow(endContext);
    }
}
