package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ProjectProfitSharingFlow;

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
