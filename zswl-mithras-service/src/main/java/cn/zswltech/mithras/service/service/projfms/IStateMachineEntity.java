package cn.zswltech.mithras.service.service.projfms;

import cn.zswltech.mithras.common.enums.RecordStatus;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/5 14:15
 */
public interface IStateMachineEntity {

    Long getId();

    void setId(Long id);

    ProcessStatus getProcessStatus();

    void setProcessStatus(ProcessStatus processState);

    RecordStatus getRecordStatus();

    void setRecordStatus(RecordStatus recordStatus);

    String getProcessStatusFieldName();

    String getRecordStatusFieldName();
}
