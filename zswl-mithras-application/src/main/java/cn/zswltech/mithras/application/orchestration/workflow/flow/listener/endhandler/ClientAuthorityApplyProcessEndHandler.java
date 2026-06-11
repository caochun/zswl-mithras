package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.application.orchestration.client.ClientAuthorityApplyService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.*;

/**
 * 客户模块流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class ClientAuthorityApplyProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private ClientAuthorityApplyService authorityApplyService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ClientApplyAuthorityFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        authorityApplyService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }

}
