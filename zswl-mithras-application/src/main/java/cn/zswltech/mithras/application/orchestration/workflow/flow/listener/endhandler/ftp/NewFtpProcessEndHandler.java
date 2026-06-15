package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.ftp;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.ftp.newftp.service.NewFtpVersionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/12 19:33
 */
@Component
public class NewFtpProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private NewFtpVersionService newFtpVersionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.FtpMonthlyGuidanceModifyFlow.name(), ProcessModelTypeEnum.FtpMonthlyGuidanceCreateFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        newFtpVersionService.processEnd(Long.valueOf(endContext.getBusinessKey()),
                ProcessBusinessStatusEnum.success(endContext.getEndType()),
                ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType()),
                Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
    }
}
