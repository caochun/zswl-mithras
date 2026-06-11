package cn.zswltech.mithras.workflow.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;

/**
 * 流程结束处理器
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:35 PM
 */
public abstract class AbstractProcessEndHandler {

    public boolean needHandle(ProcessEndContext endContext) {
        return true;
    }

    public abstract void handle(ProcessEndContext endContext);
}
