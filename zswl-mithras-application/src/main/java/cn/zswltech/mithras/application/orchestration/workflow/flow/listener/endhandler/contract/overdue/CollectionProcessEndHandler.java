package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.contract.overdue;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionApplicationService;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.OverdueCollectionLetterAuditFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.OverdueLawerLetterAuditFlow;

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
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        collectionApplicationService.processEnd(Long.valueOf(endContext.getBusinessKey()), processPass,
                Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }

}
