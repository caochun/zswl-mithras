package cn.zswltech.mithras.finance.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.financeprojectdistribution.service.impl.FinanceProjectDistributionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ProjectProfitSharingFlow;

/**
 * @create: 2023-03-20
 **/
@Component
public class FinanceProjectDistributionEndHandler extends AbstractProcessEndHandler {

    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProjectProfitSharingFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        financeProjectDistributionService.processEnd(endContext);
    }
}
