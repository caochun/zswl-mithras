package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.blackgray;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.blackgray.application.audit.BlackGrayOutboundAuditService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 合同创建
 *
 */
@Component
public class BlackGrayOutboundEndWorker extends AbstractProcessEndHandler {

    @Resource
    private BlackGrayOutboundAuditService blackGrayOutboundAuditService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.BLACK_GRAY_OUTBOUND.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        blackGrayOutboundAuditService.finish(Long.parseLong(endContext.getBusinessKey()));
    }

}
