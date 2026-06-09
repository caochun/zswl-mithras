package cn.zswltech.mithras.rating.flow;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.rating.service.RatingClientService;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.service.flow.listener.endhandler.ILifecycleProcessor;
import cn.zswltech.mithras.projlifecycle.mapper.model.ProjLifecycleEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.RatingClientCreateFlow;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.RatingClientUpdateFlow;

@Component
public class RatingClientProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {

    @Resource
    private RatingClientService ratingClientService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), RatingClientCreateFlow.name(),RatingClientUpdateFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        ratingClientService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
//        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("付款审批"));
    }
}
