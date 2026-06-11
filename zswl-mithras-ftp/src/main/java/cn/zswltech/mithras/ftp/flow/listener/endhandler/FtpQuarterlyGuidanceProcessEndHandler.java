package cn.zswltech.mithras.ftp.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.ftp.oldftp.service.FtpQuarterlyGuidanceService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
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
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.FtpQuarterlyGuidanceCreateFlow.name(), ProcessModelTypeEnum.FtpQuarterlyGuidanceModifyFlow.name());
    }
    @Override
    public void handle(ProcessEndContext endContext) {
        quarterlyGuidanceService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(),
                Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }
}
