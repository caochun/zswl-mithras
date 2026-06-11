package cn.zswltech.mithras.workflow.process.prepare.handle;

import cn.zswltech.mithras.workflow.mapper.model.CommonProcessPrepare;

/**
 * @ClassName FlowCommit
 * @Description 流程发起操作
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
public abstract class AbstractFlowCommitHandle {

    public abstract boolean needHandle(String processType);

    public abstract String commit(CommonProcessPrepare prepare);

    public void afterDiscard(CommonProcessPrepare prepare) {}

}
