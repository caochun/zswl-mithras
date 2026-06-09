package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.projlifecycle.mapper.model.ProjLifecycleEvent;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.ProjEstablishCreateFlow;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.ProjEstablishModifyFlow;

/**
 * 立项流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class ProjEstablishProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {

    @Resource
    private ProjEstablishService projEstablishService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProjEstablishCreateFlow.name(), ProjEstablishModifyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        projEstablishService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        endEvent.setProjId(Long.valueOf(endContext.getBusinessKey()));
        endEvent.setProjType(ReviewRelationDataType.PROJ_ESTABLISH.name());
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("立项审批"));
    }
}
