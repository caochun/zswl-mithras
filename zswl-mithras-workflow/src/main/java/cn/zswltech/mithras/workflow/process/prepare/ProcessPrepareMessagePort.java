package cn.zswltech.mithras.workflow.process.prepare;

import cn.zswltech.mithras.workflow.persistence.model.CommonProcessPrepare;

public interface ProcessPrepareMessagePort {

    void noticeMessage(CommonProcessPrepare prepare);
}
