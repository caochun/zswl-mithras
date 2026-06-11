package cn.zswltech.mithras.workflow.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.RentPaymentNotifyFlow;

/**
 * 客户转交流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class ProcessPrepareEndHandler extends AbstractProcessEndHandler {

    @Resource
    private CommonProcessPrepareService service;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), RentPaymentNotifyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        service.processApproved(endContext.getBusinessKey(), endContext.getEndType(), endContext.getProcessInstanceId());
    }

}
