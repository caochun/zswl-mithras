package cn.zswltech.mithras.assetclassify.application.job;

import cn.zswltech.flow.core.domain.resp.TaskResp;

public interface AssetClassifyAutoPassExecutionPort {

    void passAll(String processInstanceId, String message);

    void currentNodeAutoCommit(TaskResp task, String handlerId, String message);
}
