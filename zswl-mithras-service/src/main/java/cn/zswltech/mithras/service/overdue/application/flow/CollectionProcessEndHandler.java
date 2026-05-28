package cn.zswltech.mithras.service.overdue.application.flow;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.service.overdue.application.service.CollectionApplicationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.OverdueCollectionLetterAuditFlow;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.OverdueLawerLetterAuditFlow;

/**
 * @author zhaozhengkang
 */
@Component
public class CollectionProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private CollectionApplicationService collectionApplicationService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), OverdueCollectionLetterAuditFlow.name(), OverdueLawerLetterAuditFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        collectionApplicationService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }

}
