package cn.zswltech.mithras.workflow.application.process.prepare;

import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;

public interface ProcessPrepareMessagePort {

    void noticeMessage(CommonProcessPrepare prepare);
}
