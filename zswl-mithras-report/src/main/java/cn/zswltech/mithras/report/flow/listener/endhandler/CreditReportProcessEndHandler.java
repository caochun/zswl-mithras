package cn.zswltech.mithras.report.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.report.handler.CrFacade;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 客户模块流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class CreditReportProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private CrFacade crFacade;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.CreditReportFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        // 此处可能存在事务问题 但如果业务处理有问题抛异常 report业务事务会回滚，流程事务也会回滚 暂不处理
        crFacade.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), LocalDateTime.now());
    }

}
