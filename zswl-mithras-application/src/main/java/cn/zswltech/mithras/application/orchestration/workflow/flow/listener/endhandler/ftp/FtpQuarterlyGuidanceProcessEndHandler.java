package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.ftp;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpGuidanceWorkflowKey;
import cn.zswltech.mithras.ftp.oldftp.service.FtpQuarterlyGuidanceService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/12 19:33
 */
@Component
public class FtpQuarterlyGuidanceProcessEndHandler extends AbstractProcessEndHandler{
    @Resource
    private FtpQuarterlyGuidanceService quarterlyGuidanceService;
    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(),
                FtpGuidanceWorkflowKey.FTP_QUARTERLY_GUIDANCE_CREATE,
                FtpGuidanceWorkflowKey.FTP_QUARTERLY_GUIDANCE_MODIFY);
    }
    @Override
    public void handle(ProcessEndContext endContext) {
        quarterlyGuidanceService.processEnd(Long.valueOf(endContext.getBusinessKey()),
                ProcessBusinessStatusEnum.success(endContext.getEndType()),
                ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType()),
                Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
    }
}
