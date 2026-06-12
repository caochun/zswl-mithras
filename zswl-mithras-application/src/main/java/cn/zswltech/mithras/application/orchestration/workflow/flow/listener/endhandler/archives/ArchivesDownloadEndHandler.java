package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.archives;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.archives.application.ArchivesManageService;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ArchivesDownloadFlow;

/**
 * @create: 2023-03-20
 **/
@Component
public class ArchivesDownloadEndHandler extends AbstractProcessEndHandler {

    @Resource
    private ArchivesManageService archivesManageService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ArchivesDownloadFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        archivesManageService.completeDownloadApproval(
                endContext.getBusinessKey(),
                ProcessBusinessStatusEnum.success(endContext.getEndType()),
                ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType()));
    }
}
